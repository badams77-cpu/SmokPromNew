package com.smokpromotion.SmokProm.services.schedule;

import com.smokpromotion.SmokProm.domain.entity.DE_TwitterSearch;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_TwitterSearch;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.smokpromotion.SmokProm.services.twitter.Search4J;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SImpleSearchSchedule {

    @Autowired
    private REP_TwitterSearch searchRepo;

    @Autowired
    private REP_UserService userRepo;

    @Autowired
    private Search4J searchService;

    @Scheduled(cron="0 5 4 * * *")
    public void scheduler(){
        List<S_User> user = userRepo.getAllActive();
        Map<Integer, S_User> userMap = user.stream().collect(Collectors.toMap(x->x.getId(), x->x));
        List<DE_TwitterSearch> searches = searchRepo.getAllActiveNotRunToday();
        for(DE_TwitterSearch s : searches){
            S_User u = userMap.get(s.getUserId());
            if (u.isUseractive()){
                searchService.searchTwitter(u.getId(), s.getId());
            }
        }
    }
}
