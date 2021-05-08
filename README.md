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

    - Find all the trajectories of a certain taxi.

![findSimilar](https://user-images.githubusercontent.com/32561992/117544323-d12ca600-b018-11eb-9181-9b9d1a225ab3.png)

    - Find all the similar trajectories with a certain taxi (red color representing the test one)

# Experiments

## partition methods - MDL, Quadratic partition

MDL:

![MDL](https://user-images.githubusercontent.com/32561992/117544639-49479b80-b01a-11eb-8409-489e6f4c3a43.png)

Quadratic:

![weightedDivided](https://user-images.githubusercontent.com/32561992/117544902-6d57ac80-b01b-11eb-98d8-63bec2f21920.png)

    Test dimension reduction strength: 100 tracks are randomly selected from 180000 tracks for uniform sampling and dimension reduction based on MDL principle. The uniform sampling dimension reduction is to sample the track points according to a specific time interval and generate the reduced track. By comparing the dimensionality reduction strength of the original trajectory and the two dimensionality reduction methods, that is, the number of trajectory points after dimensionality reduction, the actual effect of dimensionality reduction based on MDL principle is analyzed. The results are as follows:

![reductionDegree](https://user-images.githubusercontent.com/32561992/117545276-f58a8180-b01c-11eb-8605-780bc33123f7.png)

    Test feature retention: 101 tracks are randomly selected from 180000 tracks for uniform sampling and dimension reduction based on MDL principle. The Hausdorff distance between the remaining 100 tracks and the first original track and the Hausdorff distance between the remaining 100 tracks and the first original track after dimension reduction in two ways are calculated (Compared with the original trajectory, the Hausdorff distance deviation is smaller and more stable, which shows that it has better robustness).

![reductionEffect](https://user-images.githubusercontent.com/32561992/117545304-1357e680-b01d-11eb-84ea-b601efad41df.png)

## Adjust DBSCN parameters

    see the details in report.
    
## Extract important trajectory segments based on information entropy

    One target track is selected randomly, another 100 tracks are selected and extracted in proportion. The accuracy is calculated according to Hausdorff distance, and the average value is obtained by repeating ten experiments

![selectEntropy](https://user-images.githubusercontent.com/32561992/117545540-1c958300-b01e-11eb-934a-77f8b575de9c.png)

## Improved Hausdorff distance based on time constraint

    Considering the time attribute of trajectory points, it can not significantly improve the accuracy of distance calculation and trajectory matching, but it can greatly reduce
    the amount of calculation and calculation time, directly from O (n ^ 2) to o (n).
