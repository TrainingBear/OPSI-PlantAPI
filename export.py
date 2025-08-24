import tensorflow as tf
model = tf.keras.models.load_model('fast_api/model_4gap.keras')
model.export('model')

loaded = tf.saved_model.load('model')
serve = loaded.signatures['serving_default']
print(serve.inputs)
print(serve.outputs)
print(serve.structured_input_signature)
print(serve.structured_outputs)

