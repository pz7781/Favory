package project.favory.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import project.favory.common.exception.ErrorCode
import project.favory.common.exception.InternalServerException

@Service
class EmailSenderService(
    private val mailSender: JavaMailSender,
    @Value("\${app.mail.from}") private val fromAddress: String
) {
    fun sendVerificationCode(to: String, code: String) {
        val message = SimpleMailMessage().apply {
            from = fromAddress
            setTo(to)
            subject = "[Favory] 이메일 인증번호"
            text = "인증번호는 [$code] 입니다. 5분 안에 입력해주세요."
        }

        try {
            mailSender.send(message)
        } catch (ex: MailException) {
            throw InternalServerException(ErrorCode.EMAIL_VERIFICATION_SEND_FAILED)
        }
    }
}
