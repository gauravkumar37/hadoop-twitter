awk 'BEGIN {print "dummy header" > "combined.csv"}'
for csv in csv/*
do
  cat $csv |
  awk '{ if(flag!=1) {flag=1} else {
	ORS="";
	if($0 ~ /"$/) print $0 "\n" >> "combined.csv"
	else print $0 " ">> "combined.csv"
      }
   }';
done
