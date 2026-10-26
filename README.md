# Secure Vault — Tanmay Jaiswal

A Java 17 Swing password manager for a college project. Credential records are encrypted on the client with AES-256-GCM before Supabase receives them. The cloud contains only record IDs, timestamps, user IDs and ciphertext.

## Features

- Supabase Auth email/password signup and login.
- Master-password-derived encryption key (PBKDF2-HMAC-SHA-256, 600,000 iterations, 16-byte per-user local salt).
- AES-256-GCM, a random 12-byte nonce per encryption, and 128-bit tag.
- Add, edit, delete, search, show and copy credential passwords; password generation.
- Encrypted local cache in `%USERPROFILE%\.secure-vault` and best-effort cloud synchronization.
- RLS policies restrict each authenticated user to their own rows.

## Windows / VS Code setup

1. Install a 64-bit JDK 17+ and Maven; confirm `java -version` and `mvn -version` in the VS Code terminal.
2. Open this project directory in VS Code. The Java and Maven extensions can import `pom.xml`.
3. Create a Supabase project. In SQL Editor, run `supabase/schema.sql`.
4. In Supabase Authentication settings, enable email/password. For simplest college demo, either confirm the email or configure the project to skip confirmation.
5. Set the project URL and public anon/publishable key as Windows user environment variables (PowerShell):
   ```powershell
   [Environment]::SetEnvironmentVariable('SUPABASE_URL','https://YOURREF.supabase.co','User')
   [Environment]::SetEnvironmentVariable('SUPABASE_ANON_KEY','YOUR_PUBLIC_ANON_KEY','User')
   ```
   Restart VS Code after setting them. Never set a `service_role` key in this application.
6. From the project root run `mvn test` and `mvn exec:java`.

`.env.example` documents names only. The application currently reads OS environment variables; it does not load `.env`.

## Authentication and key behavior

Supabase account authentication identifies the cloud user and supplies a short-lived access token. It does not encrypt the vault. The separate master password derives a 256-bit AES key locally using PBKDF2-HMAC-SHA-256 and the random salt in `%USERPROFILE%\.secure-vault\kdf-<user-id>.salt`. Neither password nor derived key is uploaded. Supabase's anon key is a public project identifier, not a privileged credential. RLS plus the user's JWT is the authorization boundary.

To use another device, install the app, sign into the same Supabase account, and use the same master password. The first device's salt is not currently uploaded, so this starter implementation does **not** provide successful cross-device decryption yet. Transfer that 16-byte salt securely to the new device (for example, through an offline encrypted channel) and save it as the matching `kdf-<user-id>.salt` before login; never put it in Supabase or Git. A production design should store the salt as public per-user KDF metadata in a protected profile row and version the KDF parameters. Losing the master password means the data cannot be recovered.

## Security notes and known limitations

- Cloud reads and writes use the user's JWT; all payloads are ciphertext. SQL RLS is still essential because the desktop app is untrusted.
- No server-side password reset can recover a vault key. Email account reset does not reset the master password.
- This is educational software, not a security-audited password manager. The Swing UI briefly materializes secrets in JVM strings, clipboard managers may retain copied data, and the local encrypted cache/salt use the current Windows account's filesystem permissions rather than OS-backed hardware protection.
- Offline viewing and search use cached encrypted rows. Adds/edits/deletes currently require successful cloud calls, then update the local cache. Conflicting edits across devices use last write behavior and are not merged.
- Corrupt ciphertext records are skipped rather than shown as plaintext; the cloud client reports generic HTTP failures without leaking response body details.

## Tests

`CryptoServiceTest` covers round-trip encryption, wrong keys, and tampering. Full CRUD, live Supabase Auth, and cross-user RLS integration tests require a configured Supabase project and are not included as automated tests; validate those manually using two test accounts and the SQL policies. No test results are asserted here.
