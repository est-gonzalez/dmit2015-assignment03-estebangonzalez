package dmit2015.estebangonzalez.assignment03.repository;

import common.jpa.AbstractJpaRepository;
import dmit2015.estebangonzalez.assignment03.entity.OscarReview;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class OscarReviewRepository extends AbstractJpaRepository<OscarReview, Long> {

    public OscarReviewRepository() {
        super(OscarReview.class);
    }

}