# Szalvéta rendező alkalmazás

## A napkin_chunk.tflite létrehozása.
A Google Colab-on használt python kód.

<code>

!wget https://url_to_napkin_zip.hu/napkin.zip
!unzip napkin.zip

import tensorflow as tf
import matplotlib.pyplot as plt

img_width, img_height = 224, 224
batch_s = 10

! A train, validation, és teszt könyvtárakban vannak az egyes kategóriákhoz tartozó képek.
train_ds = tf.keras.utils.image_dataset_from_directory(
    "napkin/train",
    image_size = (img_width, img_height),
    batch_size = batch_s
)
validation_ds = tf.keras.utils.image_dataset_from_directory(
    "napkin/validation",
    image_size = (img_width, img_height),
    batch_size = batch_s
)
test_ds = tf.keras.utils.image_dataset_from_directory(
    "napkin/test",
    image_size = (img_width, img_height),
    batch_size = batch_s
)

! 6 kategóriám van jelenleg.
class_names = [ "coca", "dot", "form", "glass_coaster", "ice_cream", "one_colored" ]
plt.figure(figsize=(10, 10))
for images, labels in train_ds.take(1):
  for i in range(9):
    ax = plt.subplot(3, 3, i + 1);
    plt.imshow(images[i].numpy().astype("uint8"))
    plt.title(class_names[labels[i]])
    plt.axis("off")

! Az utolsó előtti layer 64 elemű, ennyi tulajdonságot néz a végén, hogy az utolsó layer-ben eldöntse, hogy mely 6 kategóriába
! milyen eséllyel tartozik az adott kép.
model = tf.keras.Sequential(
    [
        tf.keras.layers.Rescaling(1./255),
        tf.keras.layers.Conv2D(32, 3, activation="relu"),
        tf.keras.layers.MaxPooling2D(),
        tf.keras.layers.Conv2D(32, 3, activation="relu"),
        tf.keras.layers.MaxPooling2D(),
        tf.keras.layers.Conv2D(32, 3, activation="relu"),
        tf.keras.layers.MaxPooling2D(),
        tf.keras.layers.Flatten(),
        tf.keras.layers.Dense(128, activation="relu"),
        tf.keras.layers.Dense(64),
        tf.keras.layers.Dense(6),
    ]
)

model.compile(
    optimizer="adam",
    loss=tf.losses.SparseCategoricalCrossentropy(from_logits="True"),
    metrics=['accuracy']
)

model.fit(
    train_ds,
    validation_data = validation_ds,
    epochs = 10
)

model.evaluate(test_ds)

import numpy

plt.figure(figsize=(10, 10))
for images, labels in test_ds.take(1):
  classifications = model(images)

  for i in range(9):
    ax = plt.subplot(3, 3, i + 1)
    plt.imshow(images[i].numpy().astype("uint8"))
    index = numpy.argmax(classifications[i])
    plt.title("Felismert: " + str(class_names[index] == class_names[labels[i]]) + ", " + class_names[index] + " == " + class_names[labels[i]])

converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

! A napkin_full.tflite az a neuronháló, ami kategóriákra bont.
with open("napkin_full.tflite", 'wb') as f:
  f.write(tflite_model)

! Az utolsó layer-t kihagyjuk, mert nem a kategóriát, hanem az előtte lévő tulajdonság réteget akarom használni az app-ban.
model.pop();

print(len(model.layers));

converter_chunk = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_chunk_model = converter_chunk.convert()

! Elmentjük a tulajdonság réteggel. Ezt használjuk az app-ban.
with open("napkin_chunk.tflite", 'wb') as f:
  f.write(tflite_chunk_model)

</code>
