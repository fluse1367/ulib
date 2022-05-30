# Code Rules / Code of Conduct

## Everywhere

- Keep static members separated from instanced ones (e.g. designate the upper half of the class for static members and
  the lower for instance ones).
- Use [JetBrains annotations](https://www.jetbrains.com/help/idea/annotating-source-code.html#bundled-annotations) to
  indicate behavior (especially `NonNull`, `Nullable` and `Contract`). Functional interfaces are exempt.

## API

- Use `Optional` if a method may not have a value to return, **never** return `null`.
- Methods may not declare any throwing Exceptions;
  Use [`Expect`](core-api/src/main/java/eu/software4you/ulib/core/api/util/value/Expect.java) to wrap any underlying
  exception.
- Document classes/methods with javadocs.
- No major static code: use interfaces in conjunction with service providers and final classes with static accessor
  methods instead (e.g. the interface `SomeApiInst` and the final class `SomeApi`)
- Service interfaces must have a static `get` method to obtain their service instance.

## Implementation

- Returning `null` values is explicitly permitted.
- Document classes/methods with javadocs or regular comments.

---
I am well aware that of writing this the library does not fully comply with these rules. However, it's a long term goal
for already existing code.