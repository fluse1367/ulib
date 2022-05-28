[<- Back to Overview](../Readme.md)

## uLib Build Instructions

---

Building ulib from source is super easy. Clone the repository and build it with gradle.

Note: You need to have the JDK 17 installed.

1. **Clone this repository**
   ```shell
   git clone https://gitlab.com/software4you.eu/ulib.git
   ```
2. **`cd` into the directory**
   ```shell
   cd ulib
   ```
3. <details><summary><b>Switch to another branch</b> (Optional)</summary>

   ```shell
   git checkout BRANCH_NAME
   ```
   </details>


4. **Build it**

   Linux (bash):

   ```shell
   ./gradlew build
   ```

   Windows (cmd):

   ```shell
   ./gradlew.bat build
   ```

   You will find the loader in `loader/build/libs/`.