# whisker
Visualize Refactoring-impact.

## Usage

for general project.

```
$ git clone https://github.com/yo1000/whisker.git
$ cd whisker
$ mvn spring-boot:run
```

for japanese project.

```
$ git clone https://github.com/yo1000/whisker.git
$ cd whisker
$ mvn spring-boot:run --define "application.metrics.git.comment-regex=.*(Fix|Bug|修正|バグ|対応|改修|改善|不具合|直した).*"
```
