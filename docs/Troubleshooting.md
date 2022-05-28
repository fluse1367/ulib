[<- Back to Overview](Readme.md)

## Troubleshooting

Common malfunctions/mistakes and possible fixes listed are listed below.

---

- ```
  Module some-module reads more than one module named other-module
  ```
  Some of uLib's dependencies are already loaded by a higher module layer of your runtime. Try to add the java startup
  flag `-Dulib.install.module_layer=boot`. If that doesn't work try `-Dulib.install.module_layer=comply`.