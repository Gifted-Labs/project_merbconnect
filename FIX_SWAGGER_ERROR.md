# 🔧 Fixing "Failed to load API definition" Error

## Problem
You're getting: **"Failed to load API definition. Fetch error response status is 500 /v3/api-docs"**

## Root Cause
The application hasn't been rebuilt after adding the Swagger annotations. The Java compiler needs to recompile all the files with the new annotations.

## Solution - Follow These Steps:

### Step 1: Clean Build (Required)
```bash
cd C:\Users\aaa\Documents\merbsconnect
mvn clean
```

### Step 2: Compile
```bash
mvn compile
```

### Step 3: Build Full Package
```bash
mvn clean install -DskipTests
```

### Step 4: Run Application
```bash
mvn spring-boot:run
```

### Step 5: Access Swagger UI
Open browser: `http://localhost:8080/swagger-ui.html`

---

## Why This Works

1. **`mvn clean`** - Removes all previously compiled `.class` files
2. **`mvn compile`** - Recompiles all Java files with the new Swagger annotations
3. **`mvn install`** - Packages everything correctly
4. **Spring Boot** - Now recognizes all the Swagger configuration and generates the OpenAPI spec

---

## If It Still Doesn't Work:

### Check Java Version
```bash
java -version
```
Should be Java 21+

### Check for Compilation Errors
```bash
mvn clean compile 2>&1 | grep -i error
```

### View Full Application Logs
When running `mvn spring-boot:run`, look for lines like:
```
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080
```

### Verify Dependencies
```bash
mvn dependency:tree | grep springdoc
```
Should show: `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0`

---

## Common Issues & Fixes

### Issue: Port 8080 already in use
**Fix**: 
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```
Then access: `http://localhost:8081/swagger-ui.html`

### Issue: Still getting 500 error
**Fix**: Clear browser cache (Ctrl+Shift+Delete) and refresh

### Issue: Maven command not found
**Fix**: Navigate to project directory first:
```bash
cd C:\Users\aaa\Documents\merbsconnect
mvn -v  # Check Maven is installed
```

---

## Step-by-Step Command

Copy and paste this entire command (it does everything at once):

```bash
cd C:\Users\aaa\Documents\merbsconnect && mvn clean install -DskipTests && mvn spring-boot:run
```

Then wait for the message: **"Tomcat started on port(s): 8080"**

Once you see that, open: **`http://localhost:8080/swagger-ui.html`**

---

## Verify It's Working

You should see:
✅ **Swagger UI loads** (no error message)  
✅ **"MerbsConnect API" header** appears  
✅ **"Authentication" section** with 7 endpoints  
✅ **"Try it out" button** on each endpoint  

If you see all these, **you're good!** 🎉

---

**Try this now and let me know if you still get the error!**

