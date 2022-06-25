# Code Rules / Code of Conduct

## Everywhere

- Keep static members separated from instanced ones (e.g. designate the upper half of the class for static members and
  the lower for instance ones).
- Use [JetBrains annotations](https://www.jetbrains.com/help/idea/annotating-source-code.html#bundled-annotations) to
  indicate behavior (especially `NonNull`, `Nullable` and `Contract`). Functional interfaces are exempt.

## API

- Use `Optional` if a method may not have a value to return, only return `null` if it's really necessary
  (such as in [Callback#getReturnValue](core/src/main/java/eu/software4you/ulib/core/inject/Callback.java)).
- Methods may not declare any checked Exceptions;
  Use [`Expect`](core/src/main/java/eu/software4you/ulib/core/util/Expect.java) to wrap any underlying
  checked exception.
- Document classes/methods with javadocs.
- Keep the API clean, put the heavy code into the implementation.

## Implementation

- Returning `null` values is explicitly permitted.
- Document classes/methods with javadocs or regular comments.

---
I am well aware that of writing this the library does not fully comply with these rules. However, it's a long term goal
for already existing code.