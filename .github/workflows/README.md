# GitHub Actions Workflows

This directory contains CI/CD workflows for the Hermes logging library.

## Workflows

### 1. CI Build & Test (`ci.yml`)
**Purpose:** Continuous Integration pipeline that validates builds and runs tests

**Triggers:**
- Push to `master`, `main`, or `develop` branches
- Pull requests to these branches

**What it does:**
- Builds all Maven modules on Java 17, 21, and 23
- Runs test suite (JUnit 5)
- Caches Maven dependencies for faster builds
- Uploads test results and build artifacts

**Matrix strategy:** Tests against 3 Java versions in parallel

### 2. Code Quality (`code-quality.yml`)
**Purpose:** Enforces code quality standards and dependency hygiene

**Triggers:**
- Push to `master`, `main`, or `develop` branches
- Pull requests to these branches

**What it does:**
- Runs `mvn verify` for validation
- Analyzes dependencies for issues
- Checks for available dependency updates
- Displays dependency tree
- Uploads dependency reports

### 3. Annotation Processor Validation (`annotation-processor.yml`)
**Purpose:** Validates the `@InjectLogger` annotation processing works correctly

**Triggers:**
- Push to `master`, `main`, or `develop` branches (only when relevant paths change)
- Pull requests (only when relevant paths change)

**Paths monitored:**
- `hermes-processor/**`
- `hermes-api/**`
- `hermes-examples/**`

**What it does:**
- Builds the annotation processor module
- Compiles examples to trigger annotation processing
- Verifies generated sources exist in `target/generated-sources/annotations`
- Uploads generated source files as artifacts

**Note:** This workflow is Hermes-specific and validates the core compile-time code generation feature.

### 4. Release (`release.yml`)
**Purpose:** Creates GitHub releases with JAR artifacts and automatically publishes to Maven Central

**Triggers:**
- Manual workflow dispatch via GitHub UI
- Input parameters:
  - Release type (major, minor, patch)

**What it does:**
- Extracts current version from `pom.xml`
- Builds all modules (skips tests - already validated in CI)
- Collects JAR artifacts (excludes sources/javadoc/test JARs)
- Generates release notes with module descriptions
- Creates GitHub release with version tag
- Uploads JAR files as release assets
- Automatically triggers Maven Central publishing workflow

**Usage:**
1. Go to Actions tab in GitHub
2. Select "Release" workflow
3. Click "Run workflow"
4. Choose release type
5. Click "Run workflow" button

**Note:** Every release is automatically published to Maven Central.

### 5. Deploy Documentation (`docs.yml`)
**Purpose:** Builds and deploys MkDocs documentation to GitHub Pages

**Triggers:**
- Push to `master` or `main` branches (only when docs change)
- Pull requests to these branches (only when docs change)
- Manual workflow dispatch via GitHub UI

**Paths monitored:**
- `docs/**`
- `.github/workflows/docs.yml`

**What it does:**
- Sets up Python environment with pip caching
- Installs MkDocs Material theme and extensions
- Builds documentation with strict mode (fails on warnings)
- Uploads Pages artifact for deployment
- Deploys to GitHub Pages (only on push to master)

**Permissions required:**
- `contents: read` - Read repository files
- `pages: write` - Deploy to GitHub Pages
- `id-token: write` - OIDC token for deployment

**Prerequisites:**
- Enable GitHub Pages in repository settings
- Set Pages source to "GitHub Actions"
- Documentation will be available at: `https://dotbrains.github.io/hermes`

**Usage:**
1. Make changes to files in `docs/` directory
2. Push to `master` branch
3. Workflow automatically builds and deploys
4. Or manually trigger via Actions tab

**Note:** Pull requests only build docs (no deployment) to validate changes.

### 6. Publish to Maven Central (`maven-publish.yml`)
**Purpose:** Publishes Hermes artifacts to Maven Central for public consumption

**Triggers:**
- Manual workflow dispatch via GitHub UI
- Push of version tags (e.g., `v1.0.0`)
- Called automatically by Release workflow

**What it does:**
- Imports GPG keys for artifact signing
- Configures Maven with Sonatype OSSRH credentials
- Builds all modules with release profile (excludes `hermes-examples`)
- Generates source and javadoc JARs
- Signs all artifacts with GPG
- Deploys to Sonatype OSSRH staging repository
- Auto-releases to Maven Central (sync takes 15-30 minutes)

**Prerequisites:**
Before using this workflow, you must configure 4 GitHub Secrets:
- `OSSRH_USERNAME` - Central Portal token username
- `OSSRH_TOKEN` - Central Portal token
- `GPG_PRIVATE_KEY` - Base64-encoded GPG private key
- `GPG_PASSPHRASE` - GPG key passphrase

**See:** `PUBLISHING.md` for complete setup instructions

**Usage:**
1. Go to Actions tab in GitHub
2. Select "Publish to Maven Central" workflow
3. Click "Run workflow"
4. Click "Run workflow" button

**Alternative:** Push a version tag to trigger automatically:
```fish
git tag v1.0.0
git push origin v1.0.0
```

**Note:** The `hermes-examples` module is always excluded from publishing.

## Viewing Workflow Results

### In GitHub UI
- Go to the **Actions** tab in the repository
- Click on a workflow run to see details
- View logs for each step
- Download artifacts from successful runs

### Artifacts Available
- **Test results** (from CI workflow): JUnit XML reports
- **Maven artifacts** (from CI workflow): Built JAR files
- **Generated sources** (from annotation processor workflow): Generated Java files
- **Dependency reports** (from code quality workflow): Dependency analysis
- **Documentation site** (from docs workflow): Built MkDocs site
- **Release JARs** (from release workflow): Production-ready artifacts
- **Published artifacts** (from maven-publish workflow): Available on Maven Central

## Workflow Status Badges

Add these to your README.md to show workflow status:

```markdown
![CI Build & Test](https://github.com/dotbrains/hermes/workflows/CI%20Build%20%26%20Test/badge.svg)
![Code Quality](https://github.com/dotbrains/hermes/workflows/Code%20Quality/badge.svg)
![Annotation Processor](https://github.com/dotbrains/hermes/workflows/Annotation%20Processor%20Validation/badge.svg)
![Deploy Documentation](https://github.com/dotbrains/hermes/workflows/Deploy%20Documentation/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.dotbrains/hermes-parent.svg?label=Maven%20Central)
```

## Customization

### Adding Java Versions
Edit `ci.yml` matrix:
```yaml
matrix:
  java: [ '17', '21', '23', '24' ]  # Add version here
```

### Changing Trigger Branches
Edit the `on` section in any workflow:
```yaml
on:
  push:
    branches: [ master, main, develop, feature/* ]  # Add branches
```

### Adjusting Maven Memory
Change `MAVEN_OPTS` in any workflow:
```yaml
env:
  MAVEN_OPTS: -Xmx2048m  # Increase from 1024m
```

## Troubleshooting

### Build Failures
- Check Maven logs in workflow output
- Verify all dependencies are available
- Ensure Java version compatibility

### Annotation Processor Not Generating Files
- Verify `hermes-processor` builds successfully
- Check that examples use `@InjectLogger` annotation
- Ensure `maven-compiler-plugin` is configured with annotation processor path

### Release Workflow Fails
- Verify repository has write permissions enabled
- Check that `GITHUB_TOKEN` has sufficient permissions
- Ensure version in `pom.xml` is valid

### Maven Central Publishing Fails
- Verify all 4 required GitHub Secrets are configured correctly
- Check GPG key is properly base64-encoded
- Ensure namespace is verified on Central Portal (https://central.sonatype.com/)
- Verify version is not a SNAPSHOT when publishing from a tag
- Review `PUBLISHING.md` for detailed troubleshooting

### Documentation Deployment Fails
- Verify GitHub Pages is enabled in repository settings
- Check that Pages source is set to "GitHub Actions" (not branch)
- Ensure all markdown files in `docs/src/` are valid
- Verify MkDocs configuration in `docs/mkdocs.yml` is correct
- Test locally with `mkdocs build --strict` to catch errors

## Local Testing

Test workflows locally using [act](https://github.com/nektos/act):

```fish
# Install act
brew install act

# Run CI workflow
act -j build --container-architecture linux/amd64 --pull=false -P ubuntu-latest=catthehacker/ubuntu:full-latest

# Run code quality workflow
act -j quality --container-architecture linux/amd64 --pull=false -P ubuntu-latest=catthehacker/ubuntu:full-latest

# Run annotation processor workflow
act -j validate-processor --container-architecture linux/amd64 --pull=false -P ubuntu-latest=catthehacker/ubuntu:full-latest

# Run documentation build
act -j build -W .github/workflows/docs.yml
```

Or test MkDocs locally without act:
```fish
cd docs
mkdocs serve  # Preview at http://127.0.0.1:8000
mkdocs build --strict  # Build and check for errors
```

## Maintenance

- Update action versions periodically (e.g., `actions/checkout@v4` → `v5`)
- Review and update Java versions as new LTS versions are released
- Adjust cache keys if dependency management changes
- Update Maven plugins in workflows to match versions in `pom.xml`
