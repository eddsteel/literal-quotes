# Literal Quotes

This is a dumb script-level app that uploads quotes to [literal.club](https://literal.club). I'm using the YAML format I already use for my [twitter bot](https://github.com/eddsteel/bookbot).

It will scan a directory for YAML files (one per book) and upload quotes there to any books that don't have quotes from you yet, matching by ISBN. If anything goes wrong it will bail. Because it will skip complete or partially complete books the next time around, you can run it several times.

## Run

You can use nix-shell, or you can install java and kotlin and stuff.

You'll need the following environment variables

- LITERAL_HANDLE: your handle in literal.club (to check for existing highlights)
- LITERAL_TOKEN: your API token from literal.club (to use the API)
- BOOKS_PATH= books path resolved from `app/`, e.g. "../books"

Run `nix-shell` to get dependencies, then `gradle run` to run.

## Token

Use the login mutation described in the [API docs](https://literal.club/pages/api).

OK fine:

``` sh
$ curl -H 'Accept: application/json' -H 'Content-Type: application/json' -XPOST \
  https://literal.club/graphql \
  -d '{"query": "mutation { login(email: \"your email\", password: \"your password\") {token}}"}' | jq -r .data.login.token
```

## Quotes format

Here's an example:

```yaml
title: The Black Jacobins
author: C.L.R. James
isbn: 9780140299816
quotes:
- The rich are only defeated when running for their lives.
```
