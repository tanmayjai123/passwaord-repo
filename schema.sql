create table if not exists public.vault_records (
  id uuid primary key,
  user_id uuid not null references auth.users(id) on delete cascade default auth.uid(),
  encrypted_payload text not null,
  updated_at timestamptz not null default now(),
  constraint payload_not_empty check (length(encrypted_payload) > 0)
);
create index if not exists vault_records_user_updated_idx on public.vault_records(user_id, updated_at);
alter table public.vault_records enable row level security;
revoke all on public.vault_records from anon;
grant select, insert, update, delete on public.vault_records to authenticated;
drop policy if exists "users read own vault records" on public.vault_records;
create policy "users read own vault records" on public.vault_records for select to authenticated using (auth.uid() = user_id);
drop policy if exists "users insert own vault records" on public.vault_records;
create policy "users insert own vault records" on public.vault_records for insert to authenticated with check (auth.uid() = user_id);
drop policy if exists "users update own vault records" on public.vault_records;
create policy "users update own vault records" on public.vault_records for update to authenticated using (auth.uid() = user_id) with check (auth.uid() = user_id);
drop policy if exists "users delete own vault records" on public.vault_records;
create policy "users delete own vault records" on public.vault_records for delete to authenticated using (auth.uid() = user_id);
