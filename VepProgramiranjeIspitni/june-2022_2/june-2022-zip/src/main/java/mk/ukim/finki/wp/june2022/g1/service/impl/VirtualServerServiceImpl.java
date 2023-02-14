package mk.ukim.finki.wp.june2022.g1.service.impl;

import mk.ukim.finki.wp.june2022.g1.model.OSType;
import mk.ukim.finki.wp.june2022.g1.model.User;
import mk.ukim.finki.wp.june2022.g1.model.VirtualServer;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidUserIdException;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidVirtualMachineIdException;
import mk.ukim.finki.wp.june2022.g1.repository.UserRepository;
import mk.ukim.finki.wp.june2022.g1.repository.VirtualServerRepository;
import mk.ukim.finki.wp.june2022.g1.service.VirtualServerService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VirtualServerServiceImpl implements VirtualServerService {

    private final VirtualServerRepository virtualServerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public VirtualServerServiceImpl(VirtualServerRepository virtualServerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.virtualServerRepository = virtualServerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<VirtualServer> listAll() {
        return this.virtualServerRepository.findAll();
    }

    @Override
    public VirtualServer findById(Long id) {
        return this.virtualServerRepository.findById(id).orElseThrow(InvalidVirtualMachineIdException::new);
    }

    @Override
    public VirtualServer create(String name, String ipAddress, OSType osType, List<Long> owners, LocalDate launchDate) {
        List<User> users = this.userRepository.findAllById(owners);
        VirtualServer virtualServer = new VirtualServer(name,ipAddress,osType,users,launchDate);
        return this.virtualServerRepository.save(virtualServer);
    }

    @Override
    public VirtualServer update(Long id, String name, String ipAddress, OSType osType, List<Long> owners) {
        VirtualServer virtualServer = this.findById(id);
        virtualServer.setInstanceName(name);
        virtualServer.setIpAddress(ipAddress);
        virtualServer.setOSType(osType);

        List<User> users = this.userRepository.findAllById(owners);
        virtualServer.setOwners(users);
        return this.virtualServerRepository.save(virtualServer);
    }

    @Override
    public VirtualServer delete(Long id) {
        VirtualServer virtualServer = this.findById(id);
        this.virtualServerRepository.delete(virtualServer);
        return virtualServer;
    }

    @Override
    public VirtualServer markTerminated(Long id) {
        VirtualServer virtualServer = this.findById(id);
        virtualServer.setTerminated(true);
        this.virtualServerRepository.save(virtualServer);
        return virtualServer;
    }

    @Override
    public List<VirtualServer> filter(Long ownerId, Integer activeMoreThanDays) {
        User user = ownerId!=null ? this.userRepository.findById(ownerId).orElse((User) null): null;
        LocalDate date = activeMoreThanDays!=null ? LocalDate.now().minusDays(activeMoreThanDays) : null;
        if(user != null && date != null){
            return this.virtualServerRepository.findAllByOwnersContainingAndAndLaunchDateBefore(user,date);
        }else if(user != null){
            return this.virtualServerRepository.findAllByOwnersContains(user);
        }else if(date != null){
            return this.virtualServerRepository.findAllByLaunchDateBefore(date);
        }else {
            return this.virtualServerRepository.findAll();
        }
    }

}
