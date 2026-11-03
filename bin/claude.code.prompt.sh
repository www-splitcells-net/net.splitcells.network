#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

set -e
set -x

jqQuery='
  select(.type == "assistant") |
  .message.content[]? |
  select(.type == "text") |
  .text
  '

if [ "debug" = "$log_level" ]
then
  jqQuery='
    if .type == "stream_event" and .event.delta.type? == "text_delta" then
      .event.delta.text
    elif .type == "assistant" then
      .message.content[]? |
      select(.type == "tool_use" and .name == "Bash") |
      " \n[EXEC] " + .input.command
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

claude-code --print "$1" \
    --session-id "$2" \
    --resume "$2" \
    --permission-mode acceptEdits \
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