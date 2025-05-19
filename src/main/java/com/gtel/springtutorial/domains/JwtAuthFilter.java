package com.gtel.springtutorial.domains;

import com.gtel.springtutorial.domains.impl.JwtDomain;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtDomain jwtService;


    // được gọi với mỗi httprequest đi vào
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //declare
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // lấy token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }


        // username khác null và Chưa được xác thực
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //Tìm thông tin user trong DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //Nếu userdetails khớp với thông tin trong token
            if (jwtService.validateToken(token, userDetails)) {
                // Tạo Authentication Token đại diện cho người dùng đã được xác thực bằng JWT
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                //Gắn thêm thông tin chi tiết từ request
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //SecurityContextHolder: SpringSecurity lưu thông tin người dùng hiện tại ở đây
                // , trong suốt vòng đời của request
                //Nơi khác có thể sử dụng SecurityContextHolder.getContext().getAuthentication() để biết ai đăng nhập
                // có quyền gì
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
