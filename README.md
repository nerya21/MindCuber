# MindCuber
NXT 2.0 Rubik's Cube solver
## Projects
The repository contains three projects:
### MindCuber_PC
This is the main project of the Rubik's Cube solver contains the PC application
### MindCuber_NXT
This project contains the application that runs on the NXT, intended to receive commands from the PC application
### MindCuber_UT
This project contains the unit tests
## Getting Started
### Prerequisites
* [Java SE Development Kit 7 - 32 bit](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html)
* [LEGO® MINDSTORMS® NXT 2.0](https://www.lego.com/en-us/mindstorms/downloads/nxt-software-download)
* [leJOS NXJ](http://www.lejos.org/nxj-downloads.php)
* [Eclipse IDE for Java Developers - 32 bit](http://www.eclipse.org/downloads/eclipse-packages/)
* [leJOS NXJ Plug-in for Eclipse](https://lejos.sourceforge.io/nxt/nxj/tutorial/Preliminaries/UsingEclipse.htm)
### Build
First, build the NXT project and upload it to the brick using leJOS NXJ Plug-in in Eclipse.
Only then build the PC project and run it. Upon running it will automaticlly connect to the NXT.
### User Guide
![alt text](https://raw.githubusercontent.com/nerya21/MindCuber/master/docs/media/mainmenu.jpg)
* __Solve cube__<br/>Use this option after completing necessary calibration steps for solving the cube
* __Tests__<br/>![alt text](https://raw.githubusercontent.com/nerya21/MindCuber/master/docs/media/testsmenu.jpg)
  <br/>This menu contains all of the robot's tests recommended to run to validate all robot's operations:
  * Brute force - performs random robot's operations
  * Read color - read current color and print result to logger
  * Flip cube - perfors cube flipping operations
* __Calibration Menu__<br/>![alt text](https://raw.githubusercontent.com/nerya21/MindCuber/master/docs/media/calibrationmenu.jpg)
  <br/>This menu contains all of the robot's calibration routines:
    * Color sensor - calibrate white color reading, place white color below the sensor and press _Enter_
    * Color motor - calibrate the color sensor motor. Follow the instructions of the program to calibrate the color sensor to it's three                       optional positions. Use the _<_ and _>_ buttons to rotate the motor
    * Cube tray - calibrate the cube's tray motor. Use the _<_ and _>_ buttons to rotate the tray to be aligned
    * Proximity - read the current proximity sensor data
    * Color light - read the current background light. Use it to make sure the lighting condition of the room are suitable for the color                       sensor:
        * Light is okay<br/>![alt text](https://raw.githubusercontent.com/nerya21/MindCuber/master/docs/media/background_ok.jpg)
        * Light is too strong<br/>![alt text](https://raw.githubusercontent.com/nerya21/MindCuber/master/docs/media/background_not.jpg)
* __Pattern__<br/>![alt text](https://raw.githubusercontent.com/nerya21/MindCuber/master/docs/media/patternmenu.jpg)
  <br/>This menu contains additional patterns for cube solving:
  * Plus minus
  * 4 cross
  * Cube cube
  * Cube cube cube
### Solving the Cube
Please complete the required steps before solving the cube:
1. Run and complete the calibration methods: _Color sensor_, _Color motor_ and _Cube tray_
2. Make sure light conditions are okay with _Color light_ calibration option
3. Run the _Brute force_ test to make sure everything is okay
## Authors
TODO: complete
