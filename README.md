# BlueJ Telemetry Extension (COMP 3607 Group 10)

**Team:** Anushka Dwarika, Haeley Parbhoo, Reinaldo Singh
**Scope area:** Student coding telemetry for the BlueJ IDE

A BlueJ extension that records a student's coding session (project and class changes, edits, compilations with their errors, and program runs) and writes a JSON log and a one-page PDF report for each session. Full documentation is on the [project wiki](https://github.com/anushkad-code/COMP3607_group10_bluej-telemetry/wiki).

## Requirements

- BlueJ 5 with Extensions API 3.3 or later
- Java 21 (JDK)
- Maven 3.9 or later

## Build and test

`bluej.jar` is not stored in the repository. Copy it from your BlueJ installation first.

```
git clone https://github.com/anushkad-code/COMP3607_group10_bluej-telemetry.git
cd COMP3607_group10_bluej-telemetry
mkdir lib
copy "C:\Program Files\BlueJ\lib\bluej.jar" lib\
mvn clean verify
```

This compiles the code, runs all 110 JUnit 5 tests, checks JaCoCo coverage (at least 75%; currently 97%) and builds `target\bluej-telemetry-1.0.0.jar`. The coverage report is at `target\site\jacoco\index.html`. To run one test class: `mvn test -Dtest=StatisticsCalculatorTest`.

## Install and run

1. Close BlueJ.
2. Copy `target\bluej-telemetry-1.0.0.jar` into `C:\Program Files\BlueJ\lib\extensions2\` (this needs administrator permission).
3. Start BlueJ. **Help > Installed Extensions** should list **BlueJ Telemetry 1.0.0**.
4. Open a project and work as normal. Each BlueJ launch is one session.
5. Choose **Tools > Export Telemetry Report** at any time, or close BlueJ, to write the reports.

## Where the reports go

| File | Location |
|---|---|
| `<project>-telemetry-<date>-<time>.json` | The BlueJ project folder (the last project opened in the session) |
| `<project>-telemetry-<date>-<time>.pdf` | The same BlueJ project folder |
| `telemetry-backup.json` | `Documents\BlueJ Telemetry Reports`, saved every two minutes |

If no project was opened, the reports are saved in `Documents\BlueJ Telemetry Reports`.

## Test data

See [`test-data/`](test-data/README.md): the three use-case BlueJ projects (fully correct, partially correct, completely incorrect) and the expected JSON and PDF output for each.

## Design patterns

- **Command** (`command` package): BlueJ listeners wrap work in commands that one background thread runs, so BlueJ never freezes.
- **Observer** (`core` package): `TelemetryHub` notifies the session log and the backup of every event.
- **Template Method** (`report` package): `AbstractReportGenerator.generate()` fixes the steps that the JSON and PDF generators fill in.
