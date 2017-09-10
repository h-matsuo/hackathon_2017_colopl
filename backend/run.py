#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import base64
from datetime import datetime
import io
import json
import urllib.request

import flask
from PIL import Image
import pymongo

app = flask.Flask(__name__)
app.config['JSON_AS_ASCII'] = False # JSON の日本語文字化け対策

ORIGIN = 'http://1ffd1c85.ngrok.io'

DB_NAME = 'hikyo'
client = pymongo.MongoClient() # 接続先；localhost は void
db = client[DB_NAME]


def make_response(data, headers=None):
  res = flask.jsonify(data)
  res.headers['Content-Type'] += '; charset=UTF-8'
  return res


def reverse_geocoding(latitude, longitude):
  url = 'https://www.finds.jp/ws/rgeocode.php?json&lat={}&lon={}'.format(latitude, longitude)
  location = json.loads(urllib.request.urlopen(url).read())
  prefecture = location['result']['prefecture']['pname']
  city       = location['result']['municipality']['mname']
  return {
    'prefecture': prefecture,
    'city'      : city
  }


def resize_image(image_data):
  decoded = base64.b64decode(image_data)
  fp = io.BytesIO(decoded)
  img = Image.open(fp, 'r')
  img.thumbnail((500, 500), Image.ANTIALIAS)
  fp = io.BytesIO()
  img.save(fp, 'JPEG', quality=100, optimize=True)
  encoded = base64.b64encode(fp.getvalue())
  return encoded


def format_spot_data(spot):
  # 画像の URL を準備
  image_urls = []
  for image_id in spot['image_ids']:
    url = '{}/api/image?image_id={}'.format(ORIGIN, image_id)
    image_urls.append(url)
  formatted = {
    'spot_id'    : spot['spot_id'],
    'spot_name'  : spot['spot_name'],
    'user_id'    : 1,
    'image_urls' : image_urls,
    'latitude'   : spot['latitude'],
    'longitude'  : spot['longitude'],
    'prefecture' : spot['prefecture'],
    'city'       : spot['city'],
    'description': spot['description'],
    'hint'       : spot['hint'],
    'favorites'  : spot['favorites'],
    'created'    : spot['created'],
    'modified'   : spot['modified']
  }
  return formatted


def db_get_spot_prefecture_list():
  result = db.spot.find()
  prefecture_list = []
  for spot in result:
    prefecture = spot['prefecture']
    if prefecture not in prefecture_list:
      prefecture_list.append(prefecture)
  return prefecture_list


def db_get_spot_city_list(prefecture):
  query = {
    'prefecture': prefecture
  }
  result = db.spot.find(query)
  city_list = []
  for spot in result:
    city = spot['city']
    if city not in city_list:
      city_list.append(city)
  return city_list


def db_get_spot_id_list(prefecture=None, city=None):
  query = {}
  if prefecture:
    query['prefecture'] = prefecture
  if city:
    query['city'] = city
  result = db.spot.find(query)
  spot_ids = []
  for spot in result:
    spot_ids.append(spot['spot_id'])
  return spot_ids


def db_get_spot(spot_id):
  query = {
    'spot_id': spot_id
  }
  result = db.spot.find(query)
  spot = None
  if result.count() > 0:
    spot = result[0]
  return spot


def db_insert_spot(spot_data):
  result = db.spot.find().sort('$natural', pymongo.DESCENDING).limit(1)
  spot_id = 1
  if result.count() > 0:
    latest_spot_id = int(result[0]['spot_id'])
    spot_id = latest_spot_id + 1
  spot_data['spot_id'] = spot_id
  db.spot.insert(spot_data)
  return spot_id


def db_insert_image(image_data):
  result = db.image.find().sort('$natural', pymongo.DESCENDING).limit(1)
  image_id = 1
  if result.count() > 0:
    latest_image_id = int(result[0]['image_id'])
    image_id = latest_image_id + 1
  db.image.insert({
    'image_id': image_id,
    'data'    : image_data
  })
  return image_id


def db_get_image(image_id):
  query = {
    'image_id': image_id
  }
  result = db.image.find(query)
  image_data = None
  if result.count() > 0:
    image_data = result[0]['data']
  return image_data


@app.route('/')
def index():
  return 'Hello, world!\nハッカソン＠せつでん村＠コロプラ株式会社\nDate: 9/9 -- 11'


@app.route('/<path:filepath>')
def debug(filepath):
  return flask.send_from_directory('./static/', filepath)


@app.route('/api/spot/list/prefecture', methods=['GET'])
def api_spot_list_prefecture():
  prefectures = db_get_spot_prefecture_list()
  result = {
    'prefectures': prefectures
  }
  return make_response(result)


@app.route('/api/spot/list/city', methods=['GET'])
def api_spot_list_city():
  req = flask.request
  prefecture = req.args.get('prefecture')
  if prefecture == None:
    flask.abort(400)
  cities = db_get_spot_city_list(prefecture)
  result = {
    'cities': cities
  }
  return make_response(result)


@app.route('/api/spot/list/id', methods=['GET'])
def api_spot_list_id():
  req = flask.request
  prefecture = req.args.get('prefecture')
  city       = req.args.get('city')
  spot_ids   = db_get_spot_id_list(prefecture, city)
  result = {
    'spot_ids': spot_ids
  }
  return make_response(result)


@app.route('/api/spot/list', methods=['GET'])
def api_spot_list():
  req = flask.request
  prefecture = req.args.get('prefecture')
  city       = req.args.get('city')
  spot_ids  = db_get_spot_id_list(prefecture, city)
  spots = []
  for spot_id in spot_ids:
    spot = db_get_spot(spot_id)
    spots.append(format_spot_data(spot))
  result = {
    'spots': spots
  }
  return make_response(result)


@app.route('/api/spot', methods=['GET'])
def api_spot():
  req = flask.request
  spot_id = req.args.get('spot_id')
  if spot_id == None:
    flask.abort(400)
  spot = db_get_spot(int(spot_id))
  if spot == None:
    flask.abort(404)
  result = format_spot_data(spot)
  return make_response(result)


@app.route('/api/spot/post', methods=['POST'])
def api_spot_post():
  req = flask.request
  if req.headers['Content-Type'] != 'application/json':
    flask.abort(400)
  params = req.json
  if ('spot_name'   not in params or
      'user_id'     not in params or
      'images'      not in params or
      'latitude'    not in params or
      'longitude'   not in params or
      'description' not in params or
      'hint'        not in params   ):
    flask.abort(400)
  # 緯度・経度から都道府県・市区町村取得
  latitude  = float(params['latitude'])
  longitude = float(params['longitude'])
  location = reverse_geocoding(latitude, longitude)
  # 画像を DB に挿入し，それらの image_id を取得
  images = params['images']
  image_ids = []
  for image_data in images:
    resized_image_data = resize_image(image_data)
    image_id = db_insert_image(resized_image_data)
    image_ids.append(image_id)
  date = datetime.now()
  spot_data = {
    'spot_name'  : params['spot_name'],
    'user_id'    : int(params['user_id']),
    'image_ids'  : image_ids,
    'latitude'   : latitude,
    'longitude'  : longitude,
    'prefecture' : location['prefecture'],
    'city'       : location['city'],
    'description': params['description'],
    'hint'       : params['hint'],
    'favorites'  : 0,
    'created'    : date,
    'modified'   : date
  }
  spot_id = db_insert_spot(spot_data)
  return make_response({'spot_id': spot_id})


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


@app.route('/api/image', methods=['GET'])
def api_image():
  req = flask.request
  image_id = req.args.get('image_id')
  if image_id == None:
    flask.abort(400)
  image_encoded = db_get_image(int(image_id))
  if image_encoded == None:
    flask.abort(404)
  image_decoded = base64.b64decode(image_encoded)
  fp = io.BytesIO(image_decoded)
  return flask.send_file(fp, mimetype='image/jpeg')


if __name__ == '__main__':
  app.run(debug = True)
