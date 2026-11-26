#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Persisting sessions automatically is hard and is therefore not done for now.
# Furthermore, most prompts are executed weekly and thereby do not benefit that much from session reuse. 

set -e

# Skills are invoked via the `Skill` tool, whose input contains the name of the used skill.
jqQuery='
  select(.type == "assistant") |
  .message.content[]? |
  if .type == "text" then
    .text
  elif .type == "tool_use" and .name == "Skill" then
    "[SKILL] " + (.input.skill // "unknown") + (if .input.args then " " + (.input.args | tostring) else "" end)
  else
    empty
  end
  '

if [ "debug" = "$log_level" ]
then
  jqQuery='
    if .type == "stream_event" and .event.delta.type? == "text_delta" then
      .event.delta.text
    elif .type == "assistant" then
      .message.content[]? |
      if .type == "tool_use" and .name == "Bash" then
        " \n[EXEC] " + .input.command
      elif .type == "tool_use" and .name == "Skill" then
        " \n[SKILL] " + (.input.skill // "unknown") + (if .input.args then " " + (.input.args | tostring) else "" end)
      else
        empty
      end
    elif .type == "user" then
      .message.content[]? |
      select(.type == "tool_result") |
      " \n[RESULT] " + (
        if .content | type == "string" then .content
        else (.content | tostring)
        end
      )
    else
      empty
    end
    '
fi

# Provide access to all repos via `--add-dir ../`.
claude-code --print "$1" \
    --permission-mode acceptEdits \
    --add-dir ../ \
    --output-format stream-json \
    --verbose \
    --include-partial-messages \
    --allowedTools \
      "Bash(git status)" \
      "Bash(git diff *)" \
      "Bash(git log *)" \
      "Bash(git show *)" \
      "Bash(git branch *)" \
      "Bash(git rev-parse *)" \
      "Bash(git ls-files *)" \
      "Bash(git blame *)" \
      "Bash(git tag *)" \
      "Bash(git remote *)" \
    | jq --unbuffered -r "$jqQuery"

echo '


Waiting until all changes by Claude Code are commited or disregarded.'

while true; do
  git diff --quiet && git diff --cached --quiet && exit
  sleep 1
done