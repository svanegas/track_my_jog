# Track My Jog - Server

It has a simple RESTful API so that it can be used with ease.

## Table of contents

  * [Dependencies](#dependencies)
  * [Setup](#setup)
  * [How to get Track My Jog up and running](#how-to-get-track-my-jog-up-and-running)
  * [Running tests](#running-tests)

## Dependencies

Track My Jog server is powered by great tech. Make sure these are present in your server box:

- [Ruby]\* 2.4.0+
- [Rails]\* 5.0.3+
- [PostgreSQL] 9.6+

To install PostgreSQL in _Linux_ you should execute the following command:

```ssh
$ apt-get install postgresql-9.3 postgresql-contrib-9.3 libpq-dev
```

Or you can install it through [Homebrew] if you are using *OS X*.

```ssh
$ brew update
$ brew install postgres
```

\* Recommended to install with [RVM](https://rvm.io/).

## Setup

Make sure you have a PostgreSQL role named as your current user with ``CREATEDB`` and
``SUPERUSER``\* attributes, and to match the database configuration in ``config/database.yml``,
where all database settings are found.

```
$ createuser $USER
```

\* ``SUPERUSER`` attribute is required because primary keys are auto-generated uuids, which will
require the installation of a PostgreSQL module that can only be installed with the ``SUPERUSER``
attribute.

> For development environemnt, no password will be required for the postgres role. But you can
> configure this setting in `config/database.yml`

Once the database role is configured, run

```sh
$ gem install bundler
$ bundle
$ rake db:create
$ rake db:migrate
```

The above commands will, respectively:

- Install [Bundler]
- Install Track My Jog's dependencies.
- Create the database
- Create all tables and columns

## How to get Track My Jog up and running

*Inside Track My Jog's core folder:*

```sh
$ rails s
```
You'll see Puma's output indicating the port the server is running on, as well
as the amount of workers it's running. This configuration can be found in
Track My Jog's ``config`` folder, in ``puma.rb``, where you can edit it.


## Running tests

Track My Jog has unit and integration tests, they are written using [RSpec], to execute them simply
run the following command:

```
$ rspec
```

And it will run all defined tests under `spec/` folder.

[Ruby]:https://rvm.io/
[Rails]:http://rubyonrails.org/
[PostgreSQL]:http://www.postgresql.org/download/
[Bundler]:http://bundler.io/
[RSpec]:http://rspec.info/
