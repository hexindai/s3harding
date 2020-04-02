s3harding
===

[![Github Workflows](https://github.com/hexindai/s3harding/workflows/ci-with-gradle/badge.svg)](https://github.com/hexindai/s3harding/actions?query=workflow%3Aci-with-gradle)

**Work in progress. Do not use it in a production deployment. 😄**

## Introduction

A simple sharding strategy based on key sharding. 

Rewriting SQL for sharding. We can custom any sharding strategy based shard key which is used for generating sharded SQL.
Currently, shards are logical that all shards are in a database node(this is dumb). In future, it will support separate
database nodes.

### Features:

* Key based sharding (hash based sharding)
* Only one database node (dumb)

## Usages

* [s3harding-core](https://github.com/hexindai/s3harding/tree/master/s3harding-core)
* [s3harding-mybatis](https://github.com/hexindai/s3harding/tree/master/s3harding-mybatis)

## Contribution

If you find some bugs, please commit your PRs.