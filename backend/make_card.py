#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

from datetime import datetime

import pymongo

DB_NAME = 'hikyo'
client = pymongo.MongoClient() # 接続先；localhost は void
db = client[DB_NAME]

CARD_DATA = {
  'card_name'  : '富士山うきうきハイキングコース♪',
  'user_id'    : 1,
  'spot_ids'   : [3, 4, 5],
  'description': '楽しみながら健康的に身体を動かしましょう！',
  'difficulty' : 3
}

def db_get_spot(spot_id):
  query = {
    'spot_id': spot_id
  }
  result = db.spot.find(query)
  spot = None
  if result.count() > 0:
    spot = result[0]
  return spot

def db_insert_card(card_data):
  result = db.card.find().sort('$natural', pymongo.DESCENDING).limit(1)
  card_id = 1
  if result.count() > 0:
    latest_card_id = int(result[0]['card_id'])
    card_id = latest_card_id + 1
  card_data['card_id'] = card_id
  db.card.insert(card_data)
  return card_id

prefectures = []
cities = []
for spot_id in CARD_DATA['spot_ids']:
  spot = db_get_spot(spot_id)
  prefecture = spot['prefecture']
  city       = spot['city']
  if prefecture not in prefectures:
    prefectures.append(prefecture)
  if city not in cities:
    cities.append(city)
CARD_DATA['prefectures'] = prefectures
CARD_DATA['cities']      = cities

date = datetime.now()
CARD_DATA['created']  = date
CARD_DATA['modified'] = date

card_id = db_insert_card(CARD_DATA)

print('Done. card_id: {}'.format(card_id))
