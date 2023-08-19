# Fuzzy C-means clustering algorithm
A from scratch Java implementation of the fuzzy c-means algorithm

# Usage
```
java -jar "cmeans.jar" <input_file> <number of clusters>
```
Input file must contain instance features separated by blank space. Only numbers allowed.

# Example
```
java -jar "cmeans.jar" Iris.data 3
```

The **output** results are the centroid of the generated clusters.

```
0.0951082579122124	0.11716383083019953	0.029894807754382878	0.011787310946490376	

0.014891579858439075	0.018307230587125967	0.00468538566155087	0.0018479619548728329	

0.0050625275174585975	0.006200650746850733	0.001596179456045944	6.301388038697242E-4
```

# Notes
Code comments and variable names are in Portuguese.
