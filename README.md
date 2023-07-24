## Description

This is a chat application that can be used for online lectures. It has been developed as part of the TU Delft course CSE1105 Object-Oriented Programming Project.

## Group members

| 📸 | Name | Email |
|---|---|---|
| ![](https://eu.ui-avatars.com/api/?name=JZ&length=4&size=50&color=DDD&background=FF0000&font-size=0.325) | Jegor Zelenjak | J.Zelenjak@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=GS&length=2&size=50&color=DDD&background=0049ff&font-size=0.325) | Giulio Segalini | g.segalini@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=CS&length=4&size=50&color=DDD&background=777&font-size=0.325) | Codrin Socol | C.Socol@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=AP&length=4&size=50&color=DDD&background=236&font-size=0.325) | Artjom Pugatsov | a.pugatsov@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=TP&length=2&size=50&color=DDD&background=236&font-size=0.325) | Tudor Popica | T.G.Popica@student.tudelft.nl |
| ![](https://eu.ui-avatars.com/api/?name=OD&length=2&size=50&color=DDD&background=0049ff&font-size=0.325) | Oleg Danilov | O.Danilov@student.tudelft.nl |


## How to run it
Dependencies: Java runtime 13 or later, OpenJDK recommended <br>
````shell
git clone https://gitlab.ewi.tudelft.nl/cse1105/2020-2021/team-repositories/oopp-group-56/repository-template.git
cd repository-template
./gradlew assemble client:fatJar server:bootJar
````

Run client:<br>
```shell
cd client/build/libs
java -jar client-AIO.jar
```

Run server:<br>
```shell
cd server/build/libs
java -jar server.jar
```

Build everything, run tests and checkstyle:<br>
```shell
./gradlew build
```

## How to contribute to it
- Fork the repository through gitlab
- Submit a merge request <br>
The merge request will not be approved if:
    - New classes/methods are not correctly tested
    - Pipeline doesn't pass or there are warning for styling
    - The additions to the code are not considered valid for this project
    - The new code does not respect the license
    
## Copyright / License (opt.)
This software is released under the GNU GPLv3 only. See LICENSE file for more information.
