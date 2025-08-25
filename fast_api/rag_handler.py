from typing import Union
from fastapi import FastAPI, UploadFile, File

import tensorflow as tf

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
    tf.image.resize(
        contents,
        (320, 320),
    )
    tf.image.convert_image_dtype(contents, tf.float32)
    predictions = model.predict(contents)
    return predictions[0].tolist()

