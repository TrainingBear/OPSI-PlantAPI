from typing import Union
from fastapi import FastAPI, UploadFile, File

import tensorflow as tf
import numpy as np
import cv2
import io

model = tf.keras.models.load_model('model_4gap.keras')
model.summary()

app = FastAPI()

dir = "uploaded_images/"
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
    path = dir + file.filename
    img_array = cv2.imread(path, cv2.IMREAD_RGB)
    # cv2.imshow(img_array)
    # cv2.waitKey(0)
    # cv2.destroyAllWindows()
    img_array = cv2.resize(img_array, (320, 320))
    predictions = model.predict(img_array)
    return predictions[0].tolist()

