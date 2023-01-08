# Ticket Guidelines

## Storage Guidelines For This Repo

Only tickets that create lasting requirements should be stored in this repo
and be preserved.
The ticketing system in this repo is not thought to be used
as a forum or a planning tool.

It documents tickets that are done and signals tickets which
are being processed or planned.
It is preferred, that planned tickets and tickets in progress should only
contain their title,
if they are stored at all.
Please store the ticket content only in this repo,
if this ticket is done.

If a done ticket is updated, this should only be done, if the update is complete,
in order to minimize the number of changes.

## General Storage Guidelines

If one needs a decentralized ticket system for unfinished tickets,
one should use dedicated repos for that,
because the storage size for such info may be too unreasonably large.
Dedicated repos might even be a good idea for done tickets as well.

Tools like [git-bug](https://github.com/MichaelMure/git-bug)
can be used fur such jobs,
but plain CommonMark documents should work too most of the time as well,
because merge conflicts caused by new comments or new ticket ids should be fairly easy to resolve most of the time.
Alternative conflicting ticket ids can be avoided via random codes like git's commit ids,
or via prefixes, that are reserved to users or special groups.
The decision between dedicated tools and plain CommonMark documents is probably mostly decided by the question,
if a versioned documents of the tickets are required or not.

## Ticket Numbering

Tickets are referenced with a hashtag and prefix in order to indicate the
server, that hosts the ticket:
for example `#g1` stands for the first Github issue and `#s1` for the first
sourcehut issue.
Ticket references without a prefix are probably hosted on
[Github](https://github.com/www-splitcells-net/net.splitcells.network/issues).

Prefix To Server Dictionary:
* g = Github: [https://github.com/www-splitcells-net/net.splitcells.network/issues](https://github.com/www-splitcells-net/net.splitcells.network/issues)
* s = sourcehut: [https://todo.sr.ht/~splitcells-net/net.splitcells.network](https://todo.sr.ht/~splitcells-net/net.splitcells.network)

Ticket information located on platforms and not in the repository are
considered throw away information.
Links to such platforms may or may not be valid.

## Content Guideline

```
# [Ticket Title/Name]

Author: [Author]

[Notices and Summary Description If Ticket Title/Name Is Not Enough]

[Subtask Progress As Nested Tree]
* [Status: TODO/DONE/PROGRESS] [Subtask Description]

[Description]
```

----
* SPDX-License-Identifier: EPL-2.0 OR MIT
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects