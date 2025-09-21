# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


from typing import Union
from fastapi import FastAPI, UploadFile, File

import tensorflow as tf
import numpy as np
import pykew.ipni as ipni
import os
os.environ["CUDA_VISIBLE_DEVICES"] = "-1"

model = tf.keras.models.load_model(os.environ.get("model"))
model.summary()

app = FastAPI()

dir = "fast_api/uploaded_images/"
os.makedirs(dir, exist_ok=True)
@app.get("/")
def read_root():
    return {"Hello": "World"}
@app.get("/items/{item_id}")
def read_item(item_id: int, q: Union[str, None] = None):
    return {"item_id": item_id, "q": q}

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    contents = await file.read()
    with open(dir + file.filename, "wb") as f:
        f.write(contents)

    contents = tf.image.decode_jpeg(contents, channels=3);
    contents = tf.expand_dims(contents, axis=1);
    contents = tf.image.resize(contents, [320, 320]);
    contents = tf.image.convert_image_dtype(contents, tf.float32);

    predictions = model.predict(contents)
    return predictions[0].tolist()

@app.get("/plants/{name}")
async def powo(name: str):
    return list(ipni.search(name))


