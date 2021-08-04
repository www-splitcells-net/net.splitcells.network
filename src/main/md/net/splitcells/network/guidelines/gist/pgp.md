# PGP Gist Guidelines

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

## Git Integration

## Helpful Detailed Guides

* [Quick-start guide to GPG](https://github.com/bfrg/gpg-guide)
* [Protecting Code Integrity With PGP](https://github.com/lfit/itpol/blob/master/protecting-code-integrity.md)
* [keys.openpgp.org Usage Guide](https://keys.openpgp.org/about/usage)