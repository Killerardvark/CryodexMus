# Project Name

Cryodex is an open source X-Wing, Imperial Assault, and Armada tournament management software.
It follows and implements the guidelines as described by the Fantasy Flight Tournament Rules for
each of these games.

## Installation

To install and run Cryodex, you need the following software:

1. A Java Runtime, Java 7 or higher.
2. The Cyrodex.jar file, as of this writing Cryodex 4.0.5 is available.

To run Cyrodex.

1. Copy the Jar to a Directory on your hard drive.
2. If using a file browser, Double Click the JAR file and it should launch.
3. Alternatively from the command prompt:  java -jar Cyrodex.jar


## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

### Building Cryodex

Maven can be used to build cryodex.  The Maven Wrapper has been provided for convience.

    ./mvnw clean install
    
This will build the Cryodex jar from the latest source, you will find the output in the target directory.

### Running Cryodex

Maven can be used to run Cryodex as well. The Maven Wrapper has been provided for convenience.

    ./mvnw package -Prun
    
This will build Cryodex and then run it.

## History


## Credits


## License

Copyright 2015 Christopher Brown

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.