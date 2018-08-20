package com.sakha.s20.ai.ModelGenerator;



import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GlobalUpdation {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> GlobalUpdator(Map<String, Object> globalMap, Map<String, Object> localMap) {
		ObjectMapper mapper = new ObjectMapper();

		Set<String> intersect = new HashSet<String>(globalMap.keySet());
		intersect.retainAll(localMap.keySet());
		String Word = null;

//			Updating the values which are in local and in global ::::::::: START
		for (String Key : intersect) {
			Map<String, Object> globalDictMap = (Map<String, Object>) globalMap.get(Key);
			Map<String, Object> localDictMap = (Map<String, Object>) localMap.get(Key);
			Map<String, Object> globalWordMap = (Map<String, Object>) globalDictMap.get("Words");
			int globalTotal = (int) globalDictMap.get("Total");
			Map<String, Object> localWordMap = (Map<String, Object>) localDictMap.get("Words");
			int localTotal = (int) localDictMap.get("Total");
			Word = (String) localWordMap.keySet().toArray()[localWordMap.size() - 1];
			int total = globalTotal + localTotal;
			Set<String> innerIntersect = new HashSet<String>(globalWordMap.keySet());
			innerIntersect.retainAll(localWordMap.keySet());
			Set<String> onlyLocalKeyset = new HashSet<String>(localWordMap.keySet());
			onlyLocalKeyset.removeAll(innerIntersect);
			Set<String> onlyGlobalKeyset = new HashSet<String>(globalWordMap.keySet());
			onlyGlobalKeyset.removeAll(innerIntersect);

//				Counting total number of NEXT WORDS ::::::: START
			Set<String> globalKeySet = globalWordMap.keySet();
			Set<String> localKeySet = localWordMap.keySet();

//				Counting total number of NEXT WORDS ::::::: STOP

//				Finding probabilities of same words ::::::: START
			if (innerIntersect.isEmpty()) {
				for (String globalKey : globalKeySet) {
					double globalProbability = (double) globalWordMap.get(globalKey);
					double Probability = (globalProbability * globalTotal) / total;
					globalWordMap.put(globalKey, Probability);
				}
				for (String localKey : localKeySet) {
					double localProbability = (double) localWordMap.get(localKey);
					double Probability = (localProbability * localTotal) / total;
					globalWordMap.put(localKey, Probability);
				}
			} else {
				for (String tempGlobalKey : innerIntersect) {
					double tempGlobalProbability = (double) globalWordMap.get(tempGlobalKey);
					double tempLocalProbability = (double) localWordMap.get(tempGlobalKey);
					double count = (tempGlobalProbability * globalTotal) + (tempLocalProbability * localTotal);
					double Probability = count / total;
					globalWordMap.put(tempGlobalKey, Probability);
				}

				for (String globalKey : onlyGlobalKeyset) {
					double tempGlobalProbability = (double) globalWordMap.get(globalKey);
					double Probability = (tempGlobalProbability * globalTotal) / total;
					globalWordMap.put(globalKey, Probability);
				}

				for (String localKey : onlyLocalKeyset) {
					double tempLocalProbability = (double) localWordMap.get(localKey);
					double Probability = (tempLocalProbability * globalTotal) / total;
					globalWordMap.put(localKey, Probability);
				}

				globalDictMap.put("Total", (localTotal + globalTotal));
			}
			LinkedHashMap<String, Double> wordDict = (LinkedHashMap<String, Double>) globalDictMap.get("Words");
			if (wordDict.containsKey(Word)) {
				wordDict = wordDict.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
				double prob = wordDict.get(Word);
				wordDict.remove(Word);
				wordDict.put(Word, prob);
				globalDictMap.replace("Words", wordDict);
			} else {
				wordDict = wordDict.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
			}
		}
//			Updating the values which are in local and in global ::::::::: STOP

//			Appending the values which are in local but not in global ::::::::: START
		Iterator<Entry<String, Object>> localItr = localMap.entrySet().iterator();
		Set<String> diff = new HashSet<String>(localMap.keySet());
		diff.removeAll(globalMap.keySet());
		while (localItr.hasNext()) {
			Entry<String, Object> localEntry = localItr.next();
			if (diff.contains(localEntry.getKey())) {
				globalMap.put(localEntry.getKey(), localEntry.getValue());
			}
		}
//			Appending the values which are in local but not in global ::::::::: STOP

		return globalMap;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
//		ObjectMapper mapper = new ObjectMapper();
//		Map<String, Object> globalMap = new HashMap<String, Object>();
//		Map<String, Object> localMap = new HashMap<String, Object>();
//		String jsonFilePath = "/home/anup/SakhaGlobal/S20/global/";
//
//		try {
//			globalMap = mapper.readValue(new File(jsonFilePath + "Model1.json"),
//					new TypeReference<Map<String, Object>>() {
//					});
//			System.out.println("GLOBAL: " + globalMap);
////			localMap = mapper.readValue(new File(jsonFilePath + "Model2.json"),
////					new TypeReference<Map<String, Object>>() {
////					});
//			mapper.writeValue(new File(jsonFilePath + "Model3.json"), GlobalUpdator(globalMap, localMap));
//		} catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}