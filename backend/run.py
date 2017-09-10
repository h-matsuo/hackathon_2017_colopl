#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from datetime import datetime
import flask
import pymongo

app = flask.Flask(__name__)
app.config['JSON_AS_ASCII'] = False # JSON の日本語文字化け対策

client = pymongo.MongoClient() # 接続先；localhost は void
db = client['hikyo']

def set_header(res):
  res.headers['Content-Type'] += '; charset=utf-8'

def make_response(data):
  res = flask.jsonify(data)
  set_header(res)
  return res

@app.route('/')
def index():
  return 'Hello, world!\nハッカソン＠せつでん村＠コロプラ株式会社'


@app.route('/api/spot/list/prefecture', methods=['GET'])
def api_spot_list_prefecture():
  result = {
    'prefectures': ['群馬', '兵庫']
  }
  return make_response(result)


@app.route('/api/spot/list/city', methods=['GET'])
def api_spot_list_city():
  req = flask.request
  prefecture = req.args.get('prefecture')
  if prefecture == None:
    flask.abort(400)
  result = {
    'cities': ['神戸']
  }
  return make_response(result)


@app.route('/api/spot/list', methods=['GET'])
def api_spot_list():
  req = flask.request
  prefecture = req.args.get('prefecture')
  city       = req.args.get('city')
  result = {
    'spot_ids': [1, 2, 3]
  }
  return make_response(result)


@app.route('/api/spot', methods=['GET'])
def api_spot():
  req = flask.request
  spot_id = req.args.get('spot_id')
  if spot_id == None:
    flask.abort(400)
  result = {
    'spot_id'    : 1,
    'spot_name'  : 'test_spot',
    'user_id'    : 1,
    'images'     : ['http://example.com'],
    'location'   : 'location',
    'prefecture' : '兵庫県',
    'city'       : '神戸市',
    'description': 'サンプルスポットです。',
    'hint'       : '三宮のどこか',
    'favorites'  : 500,
    'created'    : datetime.now(),
    'modified'   : None
  }
  return make_response(result)


@app.route('/api/spot/post', methods=['POST'])
def api_spot_post():
  req = flask.request
  spot_id = req.form['spot_id']


@app.route('/api/card/list', methods=['GET'])
def api_card_list():
  req = flask.request
  prefecture = req.args.get('prefecture')
  city       = req.args.get('city')
  pass


@app.route('/api/card', methods=['GET'])
def api_card():
  req = flask.request
  card_id = req.args.get('card_id')
  if card_id == None:
    flask.abort(400)


@app.route('/api/user', methods=['GET'])
def api_user():
  req = flask.request
  user_id = req.args.get('user_id')
  if user_id == None:
    flask.abort(400)


if __name__ == '__main__':
  app.run(debug=True)
