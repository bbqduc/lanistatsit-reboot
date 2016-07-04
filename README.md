# lanistatsit

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

### Run tests:
```
lein doo phantom test
```

## Production Build

```
lein clean
lein cljsbuild once min
```
