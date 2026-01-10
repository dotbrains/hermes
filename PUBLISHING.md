# Publishing Hermes to Maven Central

This guide explains how to publish Hermes to Maven Central using GitHub Actions.

## Prerequisites

Before you can publish to Maven Central, you need to complete these one-time setup steps:

### 1. Register on Central Portal

1. Go to https://central.sonatype.com/
2. Click "Sign Up" or "Register" (use GitHub, Google, or email)
3. Complete your profile registration
4. Navigate to **Namespaces** and register your namespace:
   - Click "Add Namespace"
   - Enter `io.github.dotbrains` as the namespace
   - Choose verification method:
     - **GitHub** (recommended): Verify via repository ownership
     - **DNS**: Add TXT record to your domain

**For GitHub verification:**
- You'll need to create a public repository: `https://github.com/dotbrains/io.github.dotbrains`
- Or use an existing repository and add the verification token as described in the portal

**Verification is instant** once you complete the required steps (no waiting for ticket approval).

### 2. Generate GPG Keys

GPG keys are required to sign artifacts for Maven Central.

```fish
# Generate a new GPG key (use RSA 4096-bit)
gpg --gen-key

# List your keys to get the key ID
gpg --list-secret-keys --keyid-format=long

# Export your private key (for GitHub Secrets)
gpg --export-secret-keys -a YOUR_KEY_ID | base64 > gpg-private.txt

# Upload your public key to key servers (OPTIONAL)
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

**Important:** Store your GPG passphrase securely - you'll need it for GitHub Secrets.

**Note:** Uploading to public keyservers is optional. Maven Central does not require your public key to be on keyservers - the GPG signatures are included with your artifacts and validated during the publishing process. If keyserver upload times out, you can proceed without it.

### 3. Generate User Token

After registering on Central Portal:

1. Log in to https://central.sonatype.com/
2. Go to your account (top right) → View Account
3. Click "Generate User Token"
4. Save the username and token (these are your publishing credentials)

### 4. Configure GitHub Secrets

Add the following secrets to your GitHub repository:

Go to: **Settings → Secrets and variables → Actions → New repository secret**

| Secret Name | Value | How to Get It |
|------------|-------|---------------|
| `OSSRH_USERNAME` | Your Central Portal token username | From Central Portal user token |
| `OSSRH_TOKEN` | Your Central Portal token | From Central Portal user token |
| `GPG_PRIVATE_KEY` | Base64-encoded GPG private key | From `gpg-private.txt` file |
| `GPG_PASSPHRASE` | Your GPG key passphrase | The password you set when creating the key |

**To get base64-encoded GPG key:**
```fish
# This creates a file with your encoded key
gpg --export-secret-keys -a YOUR_KEY_ID | base64 | pbcopy
# Now paste from clipboard into GitHub Secret
```

## Publishing Process

### Option 1: Publish from Release Workflow (Recommended)

1. Go to **Actions** tab in GitHub
2. Select **Release** workflow
3. Click **Run workflow**
4. Fill in the form:
   - **Release type**: Choose major, minor, or patch
   - **Publish to Maven Central**: Check this box ✅
5. Click **Run workflow**

This will:
1. Create a GitHub release with JARs
2. Automatically trigger Maven Central publishing
3. Publish all modules except `hermes-examples`

### Option 2: Publish Manually

1. Go to **Actions** tab in GitHub
2. Select **Publish to Maven Central** workflow
3. Click **Run workflow**
4. Choose whether to skip examples module (default: true)
5. Click **Run workflow**

### Option 3: Publish via Git Tag

Simply push a version tag to trigger automatic publishing:

```fish
git tag v1.0.0
git push origin v1.0.0
```

This automatically triggers the Maven Central publishing workflow.

## What Gets Published

The following modules are published to Maven Central:

- ✅ `hermes-api` - Core interfaces and annotations
- ✅ `hermes-core` - Implementation
- ✅ `hermes-processor` - Annotation processor
- ✅ `hermes-spring-boot-starter` - Spring Boot integration
- ✅ `hermes-kotlin` - Kotlin DSL
- ❌ `hermes-examples` - Excluded by default (not a library)

Each published module includes:
- Main JAR
- Sources JAR (`-sources.jar`)
- Javadoc JAR (`-javadoc.jar`)
- POM file
- GPG signatures (`.asc` files)

## Version Management

### Snapshot Versions

Versions ending in `-SNAPSHOT` (e.g., `1.0.0-SNAPSHOT`) are published to the snapshot repository:
- URL: https://s01.oss.sonatype.org/content/repositories/snapshots
- Available immediately
- Can be overwritten
- Not synced to Maven Central

To use snapshots in your project:
```xml
<repositories>
    <repository>
        <id>ossrh-snapshots</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### Release Versions

Versions without `-SNAPSHOT` (e.g., `1.0.0`) are published as releases:
- Automatically promoted to Maven Central
- Takes 15-30 minutes to sync
- Immutable (cannot be changed or deleted)

**Before releasing:**
1. Update version in `pom.xml` (remove `-SNAPSHOT`)
2. Commit the change
3. Run the release workflow

**After releasing:**
1. Bump version to next development version with `-SNAPSHOT`
2. Commit and push

```fish
# Example release process
mvn versions:set -DnewVersion=1.0.0
git add pom.xml */pom.xml
git commit -m "Release version 1.0.0"
git tag v1.0.0
git push origin master --tags

# Then bump to next dev version
mvn versions:set -DnewVersion=1.1.0-SNAPSHOT
git add pom.xml */pom.xml
git commit -m "Bump to 1.1.0-SNAPSHOT"
git push origin master
```

## Verifying Publication

### Check Sonatype Repository

1. Go to https://s01.oss.sonatype.org/
2. Log in with your credentials
3. Click **Staging Repositories** (left sidebar)
4. Look for `io.github.dotbrains` repository
5. View activity log to see publication status

### Check Maven Central

After 15-30 minutes, verify on Maven Central:

- Central Portal: https://central.sonatype.com/artifact/io.github.dotbrains/hermes-parent
- Maven Central Search: https://search.maven.org/search?q=g:io.github.dotbrains

### Test in a Project

Create a test Maven project:

```xml
<dependency>
    <groupId>io.github.dotbrains</groupId>
    <artifactId>hermes-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

Run:
```fish
mvn dependency:resolve
```

If successful, the artifacts are available!

## Troubleshooting

### GPG Signing Fails

**Error:** `gpg: signing failed: No such file or directory`

**Solution:**
- Verify `GPG_PRIVATE_KEY` secret is correct
- Ensure it's base64-encoded
- Check `GPG_PASSPHRASE` is correct

### Authentication Fails

**Error:** `401 Unauthorized` to Sonatype

**Solution:**
- Verify `OSSRH_USERNAME` and `OSSRH_TOKEN` are correct
- Ensure you're using **user tokens** from Central Portal, not account password
- Check your namespace is verified in Central Portal
- Regenerate your user token if needed

### Nexus Staging Fails

**Error:** `Staging rules failed`

**Solution:**
- Ensure all required files are present (JAR, POM, sources, javadoc)
- Check all artifacts are signed (.asc files)
- Verify POM has all required metadata (license, developers, SCM)

### Version Already Exists

**Error:** `Repository does not allow updating artifact: io.github.dotbrains:hermes-api:1.0.0`

**Solution:**
- Maven Central releases are immutable
- Bump version number (e.g., 1.0.1)
- Never reuse release version numbers

### Workflow Permission Denied

**Error:** `Resource not accessible by integration`

**Solution:**
- Go to Settings → Actions → General
- Under "Workflow permissions", select "Read and write permissions"
- Enable "Allow GitHub Actions to create and approve pull requests"

## Local Publishing (For Testing)

You can test the publishing process locally:

```fish
# Build with release profile (generates sources, javadoc, signs)
mvn clean install -Prelease

# Publish to local Maven repository
mvn deploy -Prelease -DaltDeploymentRepository=local::file:./target/maven-repo

# Check what would be published
ls -R target/maven-repo
```

**Note:** This requires GPG setup on your local machine.

## Best Practices

1. **Always test before releasing**
   - Run full CI build
   - Verify annotation processor works
   - Test Spring Boot integration

2. **Use semantic versioning**
   - Major: Breaking changes
   - Minor: New features (backward compatible)
   - Patch: Bug fixes

3. **Maintain changelog**
   - Document changes between versions
   - Update README with version compatibility

4. **Never publish from local machine**
   - Always use GitHub Actions
   - Ensures reproducible builds
   - Maintains audit trail

5. **Coordinate releases**
   - Publish all modules together
   - Ensure version consistency
   - Test integration between modules

## Support

If you encounter issues:

1. Check GitHub Actions logs for detailed errors
2. Review Sonatype staging repository activity log
3. Consult Central Portal documentation: https://central.sonatype.org/publish/publish-guide/
4. Email Central Support: support@central.sonatype.com

## Additional Resources

- [Central Portal Registration](https://central.sonatype.org/register/central-portal/)
- [Maven Central Publishing Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Setup Guide](https://central.sonatype.org/publish/requirements/gpg/)
- [Publishing Guide](https://central.sonatype.org/publish/publish-guide/)
- [Maven Deploy Plugin](https://maven.apache.org/plugins/maven-deploy-plugin/)
- [What Happened to issues.sonatype.org?](https://central.sonatype.org/faq/what-happened-to-issues-sonatype-org/)
