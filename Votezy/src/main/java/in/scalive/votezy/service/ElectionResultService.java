package in.scalive.votezy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.scalive.votezy.entity.Candidate;
import in.scalive.votezy.entity.ElectionResult;
import in.scalive.votezy.exception.ResourceNotFoundException;
import in.scalive.votezy.repository.CandidateRepository;
import in.scalive.votezy.repository.ElectionResultRepository;
import in.scalive.votezy.repository.VoteRepository;

@Service
public class ElectionResultService {
	
	// Repositories chaiye->
	
	//1.ElectionResultRepo=> db me result ka record add hona 
	//2.CandidateRepo=> kitne total votes aye h? => by SUM in sql
	//3.VoteRepo=> voting hui h ya nhi ?
	
	private CandidateRepository candidateRepository;
	private ElectionResultRepository electionResultRepository;
	private VoteRepository voteRepository;
	
	@Autowired
	public ElectionResultService(CandidateRepository candidateRepository,
			ElectionResultRepository electionResultRepository, VoteRepository voteRepository) {
		
		this.candidateRepository = candidateRepository;
		this.electionResultRepository = electionResultRepository;
		this.voteRepository = voteRepository;
	}
	
	// METHODS =>
	
	public ElectionResult declareElectionResult(String electionName)
	{
		Optional<ElectionResult> existingResult=this.electionResultRepository.findByElectionName(electionName);
		if(existingResult.isPresent())  // Optional ko record mil gya hai (db k andr entry hai)
         
		{
			return existingResult.get();  // return entity object   // RESULT DECLARE
		}
		
		if(voteRepository.count()==0)  // agr voting nhi hui h
		{
			throw new IllegalStateException("Cannot declare the result as no votes have been casted");
			
		}
		
		//
		List<Candidate>allCandidates=candidateRepository.findAllByOrderByVoteCountDesc();
		if(allCandidates.isEmpty())
		{
			throw new ResourceNotFoundException("No candidates available");
			
		}
		
		Candidate winner=allCandidates.get(0);  // 0 index => sbse uper wla winner
		
		int totalVotes=0;
		for(Candidate candidate:allCandidates)
		{
			totalVotes+=candidate.getVoteCount();  // sare votes aaage
		}
		
		// Object bnaa election result ka and use set kra diya name ,winner,tv se 
		
		ElectionResult result=new ElectionResult();  // entity h ye na ki spring bean so dont need to worry by new keyword
		result.setElectionName(electionName);
		result.setWinner(winner);
		result.setTotalVotes(totalVotes);
		
		return electionResultRepository.save(result);  // save & return result
			
		
	}
	
	public List<ElectionResult> getAllResults()
	{
		return electionResultRepository.findAll();
		
	}
	
	
	

}
