package de.nike.terraprotector.client.shaders;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import de.nike.terraprotector.TerraProtector;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public class TShaders {

    public final static String fragmentShader = "#version 120\n" +
            "\n" +
            "uniform float time;\n" +
            "uniform float opacity;\n" +
            "uniform vec2 resolution;\n" +
            "uniform vec4 colorSettings;\n" +
            "uniform int frag;\n" +
            "\n" +
            "float field(in vec3 p,float s) {\n" +
            "    float strength = 7. + .03 * log(1.e-6 + fract(sin(time) * 4373.11));\n" +
            "    float accum = s/4.;\n" +
            "    float prev = 0.;\n" +
            "    float tw = 0.;\n" +
            "    for (int i = 0; i < 26; ++i) {\n" +
            "        float mag = dot(p, p);\n" +
            "        p = abs(p) / mag + vec3(-.5, -.4, -1.5);\n" +
            "        float w = exp(-float(i) / 7.);\n" +
            "        accum += w * exp(-strength * pow(abs(mag - prev), 2.2));\n" +
            "        tw += w;\n" +
            "        prev = mag;\n" +
            "    }\n" +
            "    return max(0., 5. * accum / tw - .7);\n" +
            "}\n" +
            "\n" +
            "// Less iterations for second layer\n" +
            "float field2(in vec3 p, float s) {\n" +
            "    float strength = 7. + .03 * log(1.e-6 + fract(sin(time) * 4373.11));\n" +
            "    float accum = s/4.;\n" +
            "    float prev = 0.;\n" +
            "    float tw = 0.;\n" +
            "    for (int i = 0; i < 18; ++i) {\n" +
            "        float mag = dot(p, p);\n" +
            "        p = abs(p) / mag + vec3(-.5, -.4, -1.5);\n" +
            "        float w = exp(-float(i) / 7.);\n" +
            "        accum += w * exp(-strength * pow(abs(mag - prev), 2.2));\n" +
            "        tw += w;\n" +
            "        prev = mag;\n" +
            "    }\n" +
            "    return max(0., 5. * accum / tw - .7);\n" +
            "}\n" +
            "\n" +
            "vec3 nrand3( vec2 co )\n" +
            "{\n" +
            "    vec3 a = fract( cos( co.x*8.3e-3 + co.y )*vec3(1.3e5, 4.7e5, 2.9e5) );\n" +
            "    vec3 b = fract( sin( co.x*0.3e-3 + co.y )*vec3(8.1e5, 1.0e5, 0.1e5) );\n" +
            "    vec3 c = mix(a, b, 0.5);\n" +
            "    return c;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "void main() {\n" +
            "    vec3 fragCoord = frag == 1 ? gl_FragCoord.xyz : (gl_TexCoord[0].xyz);\n" +
            "\n" +
            "\n" +
            "    vec2 iResolution = resolution.xy;\n" +
            "    vec2 uv = 2. * fragCoord.xy / iResolution.xy - 1.;\n" +
            "    vec2 uvs = uv * iResolution.xy / max(iResolution.x, iResolution.y);\n" +
            "    vec3 p = vec3(uvs / 4., 0) + vec3(1., -1.3, 0.);\n" +
            "    p += .2 * vec3(sin(time / 16.0F), sin(time / 12.0F),  sin(time / 128.0F));\n" +
            "\n" +
            "    float freqs[4];\n" +
            "    //Sound\n" +
            "    freqs[0] = colorSettings.r;\n" +
            "    freqs[1] = colorSettings.g;\n" +
            "    freqs[2] = colorSettings.b;\n" +
            "    freqs[3] = colorSettings.a;\n" +
            "\n" +
            "    float t = field(p,freqs[2]);\n" +
            "    float v = (1. - exp((abs(uv.x) - 1.) * 6.)) * (1. - exp((abs(uv.y) - 1.) * 6.));\n" +
            "\n" +
            "    //Second Layer\n" +
            "    vec3 p2 = vec3(uvs / (4.+sin(time*0.11)*0.2+0.2+sin(time*0.15)*0.3+0.4), 1.5) + vec3(2., -1.3, -1.);\n" +
            "    p2 += 0.25 * vec3(sin(time / 16.), sin(time / 12.),  sin(time / 128.));\n" +
            "    float t2 = field2(p2,freqs[3]);\n" +
            "    vec4 c2 = mix(.4, 1., v) * vec4(1.3 * t2 * t2 * t2 ,1.8  * t2 * t2 , t2* freqs[0], t2);\n" +
            "\n" +
            "\n" +
            "    //Let's add some stars\n" +
            "    //Thanks to http://glsl.heroku.com/e#6904.0\n" +
            "    vec2 seed = p.xy * 2.0;\n" +
            "    seed = floor(seed * 1000);\n" +
            "    vec3 rnd = nrand3( seed );\n" +
            "    vec4 starcolor = vec4(pow(rnd.y,40.0));\n" +
            "\n" +
            "    //Second Layer\n" +
            "    vec2 seed2 = p2.xy * 2.0;\n" +
            "    seed2 = floor(seed2 * 1000);\n" +
            "    vec3 rnd2 = nrand3( seed2 );\n" +
            "    starcolor += vec4(pow(rnd2.y,40.0));\n" +
            "\n" +
            "\n" +
            "    gl_FragColor = (mix(freqs[3]-.3, 1., v) * vec4(1.5*freqs[2] * t * t* t , 1.2*freqs[1] * t * t, freqs[3]*t, opacity)+c2+starcolor);\n" +
            "}\n";

    public final static String vertexShader = "#version 120\n" +
            "\n" +
            "varying vec3 position;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
            "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
            "    gl_FrontColor = gl_Color;\n" +
            "\n" +
            "    position = (gl_ModelViewMatrix * gl_Vertex).xyz;\n" +
            "}";

    public static ShaderProgram manaSkyShader;

    public static ShaderInstance galaxyShader;

    public static void loadShaders(RegisterShadersEvent event) {
        ResourceProvider provider = event.getResourceProvider();
        try {
            galaxyShader = new ShaderInstance(provider, new ResourceLocation("galaxy"), DefaultVertexFormat.POSITION_COLOR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

