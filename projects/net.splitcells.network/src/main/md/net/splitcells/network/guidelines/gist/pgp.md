# PGP Gist Guidelines

**TOOD This document may be obsolete. Consider using SSH keys for commit signing in git instead, as PGP is too complicated.
SSH also seem to be integrated better into external tools than PGP.
When or if PGP is deprecated, do not delete this document and move it instead to a location,
that serves as an archive.
This document is than used, in order to note the project's understanding of PGP.
In other words, this document is one documented argument why PGP might or might not be used.** 

## Intro

These guidelines only show the most relevant parts for this project regarding
PGP usage.

## Quickstart For Key Creation and Management

Consider this guideline as a starting point and not a complete guide.
It should contain notes to all important operations and considerations in a
summarizing form.

Generate PGP certificate key, that is used in order to certify other keys.
It is used for nothing else.
Use the primary identity as the name and its e-mail address:
`gpg --quick-generate-key 'Example Engineer <example@example.org>' rsa4096 cert`

List all keys: `gpg --list-key`

Create subkey for signing: `gpg --quick-add-key [fingerprint  of key] ed25519 sign`
Create subkey for encryption and decryption: `gpg --quick-add-key [fingerprint  of key] cv25519 encr`

Backup `~/.gnupg` to protected storage.

Consider creating a hard copy backup of the certificate key as last resort
backup.
Hide the certificate key.
Hide the revocation certificate.

## Key Publication

Export public key to console: `gpg --export --armor [fingerprint of key]`

Upload key to public [openpgp](https://keys.openpgp.org/about/usage#gnupg-upload)
server: `gpg --export [your_address@example.net] | curl -T - https://keys.openpgp.org`.
Consider commands output and verify upload.
The public URL for the uploaded key can be used for public key distribution.

## Sign files.

Files can be signed with the default key via `gpg -ab --sign [file to sign]`.
Keyring managers may ask for passwords during this process.

## Git Integration

## Helpful Detailed Guides

* [Quick-start guide to GPG](https://github.com/bfrg/gpg-guide)
* [Protecting Code Integrity With PGP](https://github.com/lfit/itpol/blob/master/protecting-code-integrity.md)
* [keys.openpgp.org Usage Guide](https://keys.openpgp.org/about/usage)
* [Kernel Maintainer PGP guide](https://www.kernel.org/doc/html/latest/process/maintainer-pgp-guide.html)
* Consider signing tags instead of commits, as tags allow multiple people to indirectly sign a commit without chaning the commit.

----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects