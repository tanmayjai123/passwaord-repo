package com.tanmay.vault.cloud;
import com.fasterxml.jackson.databind.*;import okhttp3.*;import java.io.*;import java.util.*;
public final class SupabaseClient {
 private final String base,anon;private final OkHttpClient http;private final ObjectMapper json=new ObjectMapper();
 public SupabaseClient(String b,String a,OkHttpClient h){base=b;anon=a;http=h;}
 public List<CloudRecord> list(String token)throws IOException{Request q=builder("/rest/v1/vault_records?select=id,encrypted_payload,updated_at",token).get().build();try(Response r=http.newCall(q).execute()){return Arrays.asList(json.readValue(read(r),CloudRecord[].class));}}
 public void upsert(String token,CloudRecord row)throws IOException{RequestBody b=RequestBody.create(json.writeValueAsBytes(row),MediaType.get("application/json"));Request q=builder("/rest/v1/vault_records?on_conflict=id",token).header("Prefer","resolution=merge-duplicates,return=minimal").post(b).build();try(Response r=http.newCall(q).execute()){read(r);}}
 public void delete(String token,UUID id)throws IOException{Request q=builder("/rest/v1/vault_records?id=eq."+id,token).delete().build();try(Response r=http.newCall(q).execute()){read(r);}}
 private Request.Builder builder(String path,String token){return new Request.Builder().url(base+path).header("apikey",anon).header("Authorization","Bearer "+token).header("Content-Type","application/json");}
 private String read(Response r)throws IOException{String s=r.body()==null?"":r.body().string();if(!r.isSuccessful())throw new IOException("Supabase request failed (HTTP "+r.code()+"). Check network, schema, and RLS policy.");return s;}
 public static class CloudRecord{public UUID id;public String encrypted_payload,updated_at;public CloudRecord(){}public CloudRecord(UUID i,String p,String t){id=i;encrypted_payload=p;updated_at=t;}}
}
