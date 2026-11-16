# Test data

This folder holds the data used to check the extension by hand inside BlueJ. The automated JUnit tests build their own data in `src/test/java` (`TestData`, `SampleSessions`, `ManualClock`).

## `projects/`: the three use cases

| Folder | Class | Kind of session | What to do in BlueJ |
|---|---|---|---|
| `UseCase1` | `Greeter` | Fully correct | Compile, create `new Greeter("Class")`, call `greet()` |
| `UseCase2` | `Calculator` | Partially correct | Compile (`';' expected` on line 3), add the semicolon, compile, wait over a minute, call `add(2, 3)` then `divide(5, 0)` |
| `UseCase3` | `Broken` | Completely incorrect | Compile (incompatible types), delete `total = "ten";`, compile (cannot find symbol), delete `total = total + count;`, compile (missing return statement), close BlueJ |

To use one, copy the folder somewhere outside the repository, then in BlueJ choose **Project > Open Non BlueJ...** and pick the folder. Close BlueJ and reopen it between use cases, because each launch is one session.

## `sample-output/`: expected reports

One JSON and one PDF for each use case, produced by the extension from a scripted session that follows the steps above. A real run gives the same sections and counts, with your own times and durations.

| Use case | Compilations (failed) | Runs (exceptions) | Error types | Methods in timeline |
|---|---|---|---|---|
| 1 | 1 (0) | 2 (0) | none | `greet` |
| 2 | 2 (1) | 3 (1) | `MISSING_SEMICOLON` | `add`, `divide` |
| 3 | 3 (3) | 0 (0) | `INCOMPATIBLE_TYPES`, `CANNOT_FIND_SYMBOL`, `MISSING_RETURN` | none |

In a real run, the files are saved inside the BlueJ project folder and named `<project name>-telemetry-<date>-<time>`.
