# Chupacabra

This is a simple console app to read data from a database using JDBC.

## Compiling

Just do a `gradle jar` and look for the jar in `build/libs`.

## Usage

`$ java -jar chupacabra.jar [<options>] <query>`

Parameters:

- `query`: The query to execute

Options:
- `--connection-url`: Sets the connection url
- `--user`: Sets the user
- `--password`: Sets the password
- `--read-lobs`: Fetch lobs from database
- `--driver-class`: Set the driver class (for non JDBC 4.0 drivers)

## Including JDBC Drivers

If you don't want to include the jdbc driver on your classpath, you can build the package with it by:

1- Adding the information in `build.gradle` (look for the `drivers` extra property)
2- Running the build with `-Pjdbc=$DRIVER`
