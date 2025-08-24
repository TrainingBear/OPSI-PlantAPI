import tensorflow as tf
model = tf.keras.models.load_model('model_4gap.keras')
model.export('model')

loaded = tf.saved_model.load('model')
print("\n input:\n")
print(loaded.signatures['serving_default'].inputs)
print("\n output:")
print(loaded.signatures['serving_default'].outputs)

