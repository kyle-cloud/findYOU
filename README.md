# Preface
    With the rapid development of wireless communication technology, hundreds of millions of mobile data are generated every day in our daily life, and the mobile behavior of objects is diverse while still maintaining regularity. Therefore, this subject switches the big data information according to the base station provided by the mobile base station, excavates its path of action, identifies the petitioner according to it, and provides the petitioner with Real time location information. At the same time, in order to solve the problem of big data storage calculation and track similarity matching, the high-level and low-level models are used. The low-level model combines the segmentation and dimensionality reduction based on MDL (minimum description length) principle with the neighbor grower algorithm (DBSCAN) based on segment clustering to preprocess the track to reduce the system operation time. The high level model combines the two-level planning based on track feature extraction The improved Hausdorff distance based on information entropy, time interpolation and time constraint is combined to match the trajectory to improve the performance of similarity recognition, so as to build a big data processing environment, and finally form a web software that presents the petitioner's trajectory and similar trajectory on the map, and judges the identity information accordingly.
    
# Analysis
    - data source: T-Drive trajectory data sample (This is a sample of T-Drive trajectory dataset that contains a one-week trajectories of 10,357 taxis. The total number of points in this dataset is about 15 million and the total distance of the trajectories reaches 9 million kilometers).
    - storage: MongoDB with sharding.
    - dimensionality reduction methods: MDL principle partition for clustering line segments, Quadratic partition weighted aggregation for calculating similarity.
    - Similar trajectory identification: Time interpolation and information entropy extraction, Improved Hausdorff distance based on time constraint.
    - Software development with map: Mark the specified track, show similar tracks.
    - Optimizations: Optimize the complexity of each algorithm, Improve the algorithm according to the demand, Constructing R-tree and creating spatial index.

# Research scheme and methods
    - MDL principle partition: Based on the principle of MDL, the key track points are filtered and the track is transformed into line segments.
    - Line segments clustering with DBSCAN: Line segments clustering based on density, Fast elimination of dissimilar trajectories, Building spatial index based on R-tree
    - Quadratic partition weighted aggregation: Devide tracks according to distance and time threshold and then weighted aggregate points in the same group according to their  importance.
    - Similarity calculation: Extracting important trajectory segments based on information entropy, Time interpolation to improve accuracy, improve Hausdorff based on time constrained.

# Final visualization

https://user-images.githubusercontent.com/32561992/117544277-9aef2680-b018-11eb-8289-a9526501049a.mp4

![chooseTaxi](https://user-images.githubusercontent.com/32561992/117544321-cf62e280-b018-11eb-8e15-e743779c34fe.png)
![findSimilar](https://user-images.githubusercontent.com/32561992/117544323-d12ca600-b018-11eb-9181-9b9d1a225ab3.png)

# Experiments
