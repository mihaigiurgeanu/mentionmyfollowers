# mentionmyfollowers

FIXME: description

## Installation

Download the zip archive from freelancer website. Unzip the file. You will
get a dist folder containing the binary distribution. Before launching the
server, you may want to add your own message templates. Edit the file located
at dist/public/templates.edn then update the jar like this:

    $ cd dist/
    $ jar -uf mentionmyfollowers-0.1.0-SNAPSHOT-standalone.jar public/templates.edn

## Usage
To use the program you just need to start the server with a command like this:

    $ cd dist/
    $ java -jar mentionmyfollowers-0.1.0-SNAPSHOT-standalone.jar

## License

Copyright © 2015 Mihai Giurgeanu

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
