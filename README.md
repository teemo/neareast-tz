# neareast-tz

The goal of this script is to find the nearest timezone for a given point that does not have a timezone.

If it is less than a threshold, we replace null by the nearest timezone otherwise we leave it to null.

Input parameter:
* inputFile: path to a csv (it can be gzip)

Sample:
```
41.13375,-110.00886,America/Denver
41.13375,-109.84942,America/Denver
41.13375,-109.68999,America/Denver
41.13375,-109.53056,America/Denver
-90.0,-179.1,null
```

* threshold: the timezone will be replace if the nearest timezone is less than the threshold (in KM)


I had to custom the kd-tree to return a Point with the distance.

How to use:

mvn package

java -jar nearest-tz.jar source_file.{csv|gz} threshold_in_km
