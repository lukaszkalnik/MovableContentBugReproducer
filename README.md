# Compose 1.11.2 Typography Regression Reproducer

## Bug Summary

After upgrading from Compose BOM `2026.05.00` (Compose 1.11.1) to `2026.05.01` (Compose 1.11.2),
`MaterialTheme.typography` values are silently corrupted after navigating back from a screen that
contains a scrollable Markdown content. The text renders with the correct font family but a wrong
(much smaller) font size.

**Possibly related to:** [b/507724717](https://issuetracker.google.com/issues/507724717) —
`MultiValueMap.removeValueIf()` off-by-one fix in Compose Runtime 1.11.2.

## Required Components

Both of the following are needed to trigger the bug:

- **Navigation3** (`androidx.navigation3`) — backstack-based navigation with `backStack.add()` / `backStack.removeLastOrNull()`
- **Markdown renderer** (`com.mikepenz:multiplatform-markdown-renderer-m3`) — internally uses a composition pattern that triggers the
  corruption

Simpler alternatives (e.g. `mutableStateOf` navigation, plain `Text` with `CompositionLocalProvider`,
or explicit `movableContentOf()`) do **not** reproduce the issue.

## Conditions

- Only in **release** builds with R8 minification enabled (`isMinifyEnabled = true`)
- Requires **scrolling** on Screen B
- Does **not** occur in debug builds

## Reproduce Steps

### 1. Build the release APK

```sh
./gradlew clean assembleRelease
```

### 2. Install on a device

```sh
adb install -r app/build/outputs/apk/release/app-release.apk
```

### 3. Trigger the bug

1. Open the app → observe **"Large Title Text"** rendered at **80sp** ✅
2. Tap **"Go to Screen B"**
3. **Scroll down** at least a few items
4. Tap **"Close"** (or press the system back button) to return to Screen A
5. Observe **"Large Title Text"** is now rendered at a **much smaller size** (~14–20sp) ❌

## Expected vs Actual

|              | Font size                                            |
|--------------|------------------------------------------------------|
| **Expected** | `displayMedium` = 80sp after returning from Screen B |
| **Actual**   | ~14–20sp (falls back to a default or leaked style)   |

## Verification Matrix

| Compose BOM           | Build type | Scrolled? | Result       |
|-----------------------|------------|-----------|--------------|
| `2026.05.00` (1.11.1) | Release    | Yes       | ✅ Correct    |
| `2026.05.01` (1.11.2) | Release    | Yes       | ❌ **Broken** |
| `2026.05.01` (1.11.2) | Debug      | Yes       | ✅ Correct    |
| `2026.05.01` (1.11.2) | Release    | No        | ✅ Correct    |

## Project Structure

- **Single-module Android app**, `minSdk 29`, `compileSdk 36`
- **Compose BOM `2026.05.01`** (Compose 1.11.2)
- **Navigation3** `1.1.2` for backstack navigation
- **Markdown renderer** `0.40.2` for content on Screen B
- **Two screens**:
    - **Screen A** — displays a title using `MaterialTheme.typography.displayMedium` (80sp)
    - **Screen B** — scrollable column with Markdown-rendered content

## Filing Info

- **Component**: Jetpack > Compose > Runtime
- **Title**:
  `[Regression 1.11.2] MaterialTheme typography corrupted after navigating back from scrollable screen with Markdown content (release/R8 only)`
- **Reference**: [b/507724717](https://issuetracker.google.com/issues/507724717)

