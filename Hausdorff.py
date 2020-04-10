# coding=utf-8
from sys import argv

P = argv[1]; Q = argv[2];
sP = size(P); sQ = size(Q);
iP = repmat(1:sP(1),[1,sQ(1)]);
iQ = repmat(1:sQ(1),[sP(1),1]);
combos = [iP,iQ(:)];

cP=P(combos(:,1),:); cQ=Q(combos(:,2),:);
dists = sqrt(sum((cP - cQ).^2,2));
D = reshape(dists,sP(1),[]);
vp = max(min(D,[],2));
vq = max(min(D,[],1));
hd = max(vp,vq);
return hd