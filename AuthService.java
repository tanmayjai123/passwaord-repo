package com.tanmay.vault.auth;
import com.fasterxml.jackson.databind.*;import okhttp3.*;import java.io.*;import java.util.*;
public final class AuthService {
 private final String base,anon;private final OkHttpClient http;private final ObjectMapper json=new ObjectMapper();
 public AuthService(String b,String a,OkHttpClient h){base=b;anon=a;http=h;}
 public Session signIn(String email,String password)throws IOException{return call("/auth/v1/token?grant_type=password",email,password);}
 public Session signUp(String email,String password)throws IOException{return call("/auth/v1/signup",email,password);}
 private Session call(String path,String email,String password)throws IOException{RequestBody body=RequestBody.create(json.writeValueAsBytes(Map.of("email",email,"password",password)),MediaType.get("application/json"));Request req=new Request.Builder().url(base+path).header("apikey",anon).post(body).build();try(Response r=http.newCall(req).execute()){String text=r.body()==null?"":r.body().string();if(!r.isSuccessful())throw new IOException("Supabase authentication failed. Check email/password, confirmation settings, and network.");JsonNode n=json.readTree(text);if(n.path("access_token").isMissingNode())throw new IOException("Email confirmation may be required. Check your inbox and Supabase Auth settings.");return new Session(n.path("user").path("id").asText(),n.path("access_token").asText());}}
 public record Session(String userId,String accessToken){}
}
