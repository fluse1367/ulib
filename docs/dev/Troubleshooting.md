[<- Back to Overview](../Readme.md)

## Troubleshooting

---

Because ulib uses complex mechanics to inject itself into your desired class loader context, it is fairly easy for it to
fail. Analyzing and understanding what went wrong can be pretty tough. Common malfunctions and possible fixes listed are
listed below.

- ```
  Module ulib.core.api not found, required by mymodule
  ```
  Because uLib is loaded by the installer **after** the initialization of the boot layer, the uLib API module is not
  available at the time of initialization. Change the `requires ulib.core.api;` record in your module info file
  to `requires static`.
- ```
  class myclass (in module mymodule) cannot access class ulibclass (in module ulib.core.api) ...
  ```
  Because the `reads` record in your module info file is declared as static, you must add a `reads` record to your
  module manually before you can access the uLib API: `getClass().getModule().addReads(Installer.getModule());`

---

Please also refer to the [user troubleshooting guide](../Troubleshooting.md).