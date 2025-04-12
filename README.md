# Easy Cert Creator

A Java application that generates certificates and badges using JasperReports templates and structured text files as data sources.

## Overview

Easy Cert Creator is designed to help you generate personalized certificates and badges for events, courses, or workshops. It uses JasperReports templates (.jasper files) and structured text files containing participant information to create professional-looking PDFs.

## Features

- Generate multiple certificates or badges in batch
- Support for various data formats (structured text files)
- Flexible template system using JasperReports
- Customizable output file naming

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/easy-cert-creator-java.git
   cd easy-cert-creator-java
   ```

2. Build the application:
   ```bash
   ./mvnw clean install
   ```

## Usage

### Running the Application

Use the following command to run the application:

```bash
./mvnw exec:java -Dexec.mainClass="com.ggoncalves.easycertcreator.main.EasyCertCreatorMain" -Dexec.args="-c <jasper_file> -i <info_file> -o <output_dir>"
```

Where:
- `<jasper_file>`: Path to the compiled Jasper template (.jasper file)
- `<info_file>`: Path to the text file containing participant data
- `<output_dir>`: Directory where generated PDF files will be saved

Example:
```bash
./mvnw exec:java -Dexec.mainClass="com.ggoncalves.easycertcreator.main.EasyCertCreatorMain" -Dexec.args="-c samples/Cracha.jasper -i samples/badges.txt -o output"
```

### Creating a Runnable JAR

You can also create a standalone JAR with all dependencies:

```bash
./mvnw clean package
```

Then run it using:

```bash
java -jar target/easy-cert-creator-java-1.0-SNAPSHOT-jar-with-dependencies.jar -c <jasper_file> -i <info_file> -o <output_dir>
```

## Data File Formats

The application supports structured text files as data sources. Here are examples of supported formats:

### Certificates Format

```
#COMMON_FIELDS:programName,durationHours,programDate
#FIELDS:studentName
#SEPARATOR:;

Leadership and Social Justice Workshop;16 hours;15 de Abril de 2025

Alexandria Ocasio-Cortez
Bernie Sanders
Jacinda Ardern
Lula da Silva
Stacey Abrams
Michelle Obama
Greta Thunberg
...
```

### Badges Format

```
#FIELDS:firstName,lastName,talent1,talent2,talent3,talent4,talent5
#SEPARATOR:;

Alexandria;Ocasio-Cortez;Communication;Leadership;Advocacy;Policy;Innovation
Bernie;Sanders;Persistence;Vision;Empathy;Organizing;Authenticity
Jacinda;Ardern;Compassion;Leadership;Crisis Management;Communication;Empathy
Kamala;Harris;Strategy;Resilience;Justice;Diplomacy;Advocacy
...
```

### File Format Details

1. Headers start with `#` and define:
   - `COMMON_FIELDS`: Fields that are common to all records
   - `FIELDS`: Fields that vary for each record
   - `SEPARATOR`: Character used to separate fields (default is `;`)

2. The first non-header line after `COMMON_FIELDS` contains the values for those fields.

3. Each subsequent line corresponds to a record, with fields separated according to the defined separator.

## Creating JasperReports Templates

1. Create your template using JasperSoft Studio or another JasperReports designer
2. Compile your template (.jrxml) to produce a .jasper file
3. Use field names in your template that match the field names in your data file

### Example Parameters for Templates

For certificates:
- `programName`: Name of the program or course
- `durationHours`: Duration of the program
- `programDate`: Date of the program
- `studentName`: Name of the participant

For badges:
- `firstName`: First name of the participant
- `lastName`: Last name of the participant
- `talent1` through `talent5`: Talents or skills of the participant

## Troubleshooting

- **Missing Kotlin dependency**: If you get a `NoClassDefFoundError: kotlin/Pair` error, make sure you have the Kotlin standard library in your dependencies.
- **File not found**: Ensure all paths are correct and files exist.
- **Template errors**: Check that your .jasper file is compiled correctly and field names match those in your data file.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.