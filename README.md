

# Virtual Filesystem Simulator For Small Embedded Systems

This project is a small simulation of a basic virtual filesystem for an embedded system with primitive calls such as read & write. All reading that are captured by various sensors are stored in disk (virtual, named filesys.dat) followed by an AES encryption.
 
For the project, I demonstrated - 

 - Sensor reading generated by a random generator function in **DirWatch.java** program and stored in **envData.txt** file.
 - The file is then read by **FsController.java** (filesystem) which stores them in the **filesys.dat**.
 -  FsController.java is our main program, you need to run this program only after compiling every other file.
 - Data is a directory which acts like a camera for that embedded system, storing images which are read by the FsController.java program.
 - The data directory is being continuously monitored by the DirWatch.java program to notice any changes that may occur in that directory (say a new image is stored in that directory, the program will notice that change and write that content to our virtual disk).
 - All images in data directory are first compressed then stored on the virtual disk by the **ImageCompression.java** program.
 - For reading the data **DisplayData.java** program comes in picture which reads data from a directory created at time of read to view content of our file system.
 - And finally, **make_fs.sh** is shell script which is responsible for making the virtual disk on which our file system works.

To run the program, set this project in the classpath or add the directory to your own java project.

---

***Note*** - If you want to store your own image reading on the filesystem then you will need to 

 1. Add a new line in the following format to the envData.txt .
 
    *"[ImageName.extension]::::[Temperature]::[Humidity]::[Time(hh-mm-ss)]"*

 2. Add the corresponding image with the name *ImageName.extension* to the **data** directory.


---

**To Do**

Contributors welcome! Feel free to issue pull-requests with any new features or improvements you have come up with!

1. **Optimal use of storage** ??? Manage free space in **filesys.dat** to have minimum loss of storage and better way to access them.
2. **Parallel reading access** ??? For reading images and data in more efficient way.
3. **Remote access** ??? Remote machine capable enough to access this file systems.
4. **Sorting** ??? Sort data on basis of name or time.
5. **Bug Fixes** ??? Fix any bug/issue.

---
