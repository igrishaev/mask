# Mask

A small library to prevent secrets from being logged, printed or leaked in any
similar way. Ships tags for Clojure, EDN and Aero.

Why? Because I've been in such a situation three times, namely:

- We don't mask the secrets.
- Someone logs the entire config.
- Secrets have leaked!
- Rotate all the keys, tokens, etc.
- Change the team and face the same.

This library is an attempt to break this vicious circle.

## Installation

Leiningen/Boot:

```clojure
[com.github.igrishaev/mask "0.1.1"]
```

Clojure CLI/deps.edn:

```clojure
com.github.igrishaev/mask {:mvn/version "0.1.1"}
```

## Usage

The `mask.core` namespace provides `mask` and `unmask` functions. Pass a value
to `mask` to make it safe for logging or printing in REPL:

```clojure
(in-ns 'mask.core)
#namespace[mask.core]

(def -m (mask "Secret123"))

-m
<< masked >>

(str "The password is " -m)
"The password is << masked >>"
```

Masking is idempotent meaning that you can mask the same value multiple times
but the result will be one-level masked value:

```clojure
(-> -m mask mask mask)
<< masked >>
```

To release a value from a mask, `unmask` it:

```clojure
(unmask -m)
"Secret123"
```

Unmasking is idempotent a well:

```
(-> -m unmask unmask unmask)
"Secret123"
```

Note: the library treats `nil` as an error value that cannot be masked. You'll
get an exception:

```clojure
(mask nil)
Execution error (IllegalArgumentException) at ... (core.clj:34).
Cannot mask a nil value
```

Masking an empty value signals you're doing something wrong. Most likely you've
missed a corresponding key or an environment variable. Thus, the further work
makes no sense.

### Spec

The `mask.spec` module provides the `::mask` spec that checks if a value is
really masked. An example from the tests:

```clojure
(let [config
      {:username "Ivan"
       :password #mask "secret"}]

  (is (s/valid? ::config config)))

;; true
```

### Clojure tags

The built-in `#mask` tag wraps any value with a mask:

```clojure
=> {:token #mask "abc123" :password "SecretABC"}

{:token << masked >>, :password "SecretABC"}
```

The `#unmask` tag does the opposite: unwraps a masked value:

```clojure
(def token (mask "secret123"))

#unmask token
;; "secret123"
```

### EDN tag

There is a `reader-edn` function that acts like an EDN reader for the same tag:

```clojure
(let [source (-> "{:foo #mask 42}")]
  (edn/read-string {:readers {'mask reader-edn}}
                   source))

;; {:foo << masked >>}
```

### Aero tag

To extend Aero with the `#mask` tag, import the `mask.aero` namespace:

```clojure
(require 'mask.aero)
```

Then read a config with the tag:

```clojure
;; config.edn
{:foo #mask #env "SOME_PASSWORD"}

;; code
(aero/read-config (io/resource "config.edn"))

;; {:foo << masked >>}
```

The Aero dependency is not included. You've got to provide it by your own.

*Ivan Grishaev, 2023*
