# Setup (for macOS)

1. Python 3 インストール

    ```sh
    $ brew install python3
    ```

1. MongoDB インストール

    ```sh
    $ brew install mongodb
    ```

1. Python の依存モジュール（Flask など）をインストール

    ```sh
    $ pip3 install -r requirements.txt
    ```

1. MongoDB を実行
    ```sh
    $ mongod --config /usr/local/etc/mongod.conf
    ```

1. Flask アプリケーションを実行

    ```sh
    $ python3 run.py
    ```


# Dump & Restore MongoDB Data

以下のコマンドを実行すると，`<directory>` にデータがダンプされる：

```sh
$ mongodump -d hikyo -o <directory>
```

リストアするには以下の通り：

```sh
$ mongorestore <directory>
```

なお，本リポジトリの `backend/mongodb-dump` ディレクトリにはテスト用データが入っている。  
必要に応じてリストアすること。

* `backend/mongodb-dump/demo`
    - プレゼンのデモ時に使用したデータ
    - 地方の面白いスポットを 4 件登録
    - スタンプカードは空

* `backend/mongodb-dump/sample`
    - 実環境を想定したデータ
    - スポット，スタンプ共にそこそこの数を登録
