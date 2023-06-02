package ru.effectivemobile.socialmedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.socialmedia.model.Invitation;
import ru.effectivemobile.socialmedia.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> getInvitationByRecipientOrderByIdDesc(User recipient);
    boolean existsBySenderAndRecipient(User sender, User recipient);
    Optional<Invitation> getInvitationByRecipientAndSender(User recipient, User sender);
}
